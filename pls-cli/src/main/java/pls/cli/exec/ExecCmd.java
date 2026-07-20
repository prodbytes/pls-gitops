package pls.cli.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pls.cli.log.Logs;

@ApplicationScoped
public class ExecCmd {

    @Inject
    Logs log;

    /** Runs the command with the shell in the current working directory. */
    public ExecResult run(String command) {
        return run(command, null);
    }

    /**
     * Runs the command with the shell in the given directory, streaming each
     * output line to the log as it is produced.
     */
    public ExecResult run(String command, Path dir) {
        var workDir = dir != null ? dir : Path.of("").toAbsolutePath();
        log.debug("Executing: %s%nin: %s", command, workDir);
        var builder = new ProcessBuilder("/bin/sh", "-c", command);
        if (dir != null) {
            builder.directory(dir.toFile());
        }
        try {
            var process = builder.start();
            var stdout = new StringBuilder();
            var stderr = new StringBuilder();
            var outReader = stream(process.getInputStream(), stdout, line -> log.info("%s", line));
            var errReader = stream(process.getErrorStream(), stderr, line -> log.warn("%s", line));
            var exitCode = process.waitFor();
            outReader.join();
            errReader.join();
            return new ExecResult(exitCode, stdout.toString(), stderr.toString());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to execute: " + command, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while executing: " + command, e);
        }
    }

    private Thread stream(InputStream in, StringBuilder sink, Consumer<String> logLine) {
        return Thread.ofVirtual().start(() -> {
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
