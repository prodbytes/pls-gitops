package pls.cli.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pls.cli.log.Logs;

@ApplicationScoped
public class ExecCmd {

    @Inject
    Logs log;

    /** Runs the command in the current working directory. */
    public ExecResult run(String command) {
        return run(command, null);
    }

    /**
     * Runs the command in the given directory, streaming each output line to
     * the log as it is produced.
     *
     * The command is executed directly, without a shell, so it also works in
     * images that have no /bin/sh (e.g. FROM scratch). The command line is
     * tokenized honoring single and double quotes, and {@code &&} chains
     * commands with short-circuit on failure; other shell syntax (pipes,
     * redirection, variable expansion) is not supported.
     */
    public ExecResult run(String command, Path dir) {
        var workDir = dir != null ? dir : Path.of("").toAbsolutePath();
        log.info("🐚 [%s]$ %s", workDir, command.strip());
        var stdout = new StringBuilder();
        var stderr = new StringBuilder();
        var exitCode = 0;
        for (var argv : parse(command)) {
            exitCode = exec(argv, dir, stdout, stderr);
            if (exitCode != 0) {
                break;
            }
        }
        log.info("🐚 [%s]$ %s => exit %d (stdout %d chars, stderr %d chars)",
                workDir, command.strip(), exitCode, stdout.length(), stderr.length());
        return new ExecResult(exitCode, stdout.toString(), stderr.toString());
    }

    private int exec(List<String> argv, Path dir, StringBuilder stdout, StringBuilder stderr) {
        // Resolve a relative program path against the target directory so a
        // mounted script or binary is found regardless of the JVM's own
        // working directory. The result must be absolute: the kernel resolves
        // a relative program path against the child's cwd (the target dir),
        // which would double-apply it. Bare names (aws, git, ...) keep PATH
        // lookup.
        var program = argv.get(0);
        if (dir != null && program.contains("/") && !Path.of(program).isAbsolute()) {
            argv.set(0, dir.resolve(program).toAbsolutePath().normalize().toString());
        }
        var builder = new ProcessBuilder(argv);
        if (dir != null) {
            builder.directory(dir.toFile());
        }
        try {
            var process = builder.start();
            var outReader = stream("exec-stdout", process.getInputStream(), stdout, line -> log.info("%s", line));
            var errReader = stream("exec-stderr", process.getErrorStream(), stderr, line -> log.warn("%s", line));
            var exitCode = process.waitFor();
            outReader.join();
            errReader.join();
            return exitCode;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to execute: " + String.join(" ", argv), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while executing: " + String.join(" ", argv), e);
        }
    }

    /**
     * Splits the command line into {@code &&}-chained argument vectors,
     * honoring single and double quotes.
     */
    private List<List<String>> parse(String command) {
        var chain = new ArrayList<List<String>>();
        var argv = new ArrayList<String>();
        var token = new StringBuilder();
        var hasToken = false;
        var quote = '\0';
        for (var i = 0; i < command.length(); i++) {
            var c = command.charAt(i);
            if (quote != '\0') {
                if (c == quote) {
                    quote = '\0';
                } else {
                    token.append(c);
                }
            } else if (c == '\'' || c == '"') {
                quote = c;
                hasToken = true;
            } else if (c == '&' && i + 1 < command.length() && command.charAt(i + 1) == '&') {
                if (hasToken) {
                    argv.add(token.toString());
                    token.setLength(0);
                    hasToken = false;
                }
                if (argv.isEmpty()) {
                    throw new IllegalArgumentException("Missing command before && in: " + command);
                }
                chain.add(argv);
                argv = new ArrayList<>();
                i++;
            } else if (Character.isWhitespace(c)) {
                if (hasToken) {
                    argv.add(token.toString());
                    token.setLength(0);
                    hasToken = false;
                }
            } else {
                token.append(c);
                hasToken = true;
            }
        }
        if (quote != '\0') {
            throw new IllegalArgumentException("Unbalanced quote in: " + command);
        }
        if (hasToken) {
            argv.add(token.toString());
        }
        if (!argv.isEmpty()) {
            chain.add(argv);
        }
        if (chain.isEmpty()) {
            throw new IllegalArgumentException("Empty command: " + command);
        }
        return chain;
    }

    private Thread stream(String name, InputStream in, StringBuilder sink, Consumer<String> logLine) {
        return Thread.ofVirtual().name(name).start(() -> {
            try (var reader = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logLine.accept(line);
                    sink.append(line).append(System.lineSeparator());
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
