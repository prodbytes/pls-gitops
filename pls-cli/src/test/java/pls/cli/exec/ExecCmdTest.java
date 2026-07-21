package pls.cli.exec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class ExecCmdTest {

    @Inject
    ExecCmd exec;

    @Test
    void capturesStdoutAndExitCode() {
        var result = exec.run("echo hello");
        assertEquals(0, result.exitCode());
        assertTrue(result.success());
        assertEquals("hello" + System.lineSeparator(), result.stdout());
        assertEquals("", result.stderr());
    }

    @Test
    void honorsQuotedArguments() {
        var result = exec.run("echo \"hello world\"");
        assertEquals(0, result.exitCode());
        assertEquals("hello world" + System.lineSeparator(), result.stdout());
    }

    @Test
    void separatesStderrFromStdout() {
        var result = exec.run("bash -c 'echo out; echo err >&2'");
        assertEquals("out" + System.lineSeparator(), result.stdout());
        assertEquals("err" + System.lineSeparator(), result.stderr());
    }

    @Test
    void reportsFailureExitCode() {
        var result = exec.run("bash -c 'echo oops >&2; exit 3'");
        assertEquals(3, result.exitCode());
        assertFalse(result.success());
        assertEquals("oops" + System.lineSeparator(), result.stderr());
    }

    @Test
    void runsInGivenDirectory(@TempDir Path dir) throws IOException {
        var result = exec.run("pwd", dir);
        assertEquals(0, result.exitCode());
        assertEquals(dir.toRealPath().toString(), result.stdout().strip());
    }

    @Test
    void preservesLineOrder() {
        var result = exec.run("seq 1 5");
        var lines = result.stdout().lines().toList();
        assertEquals(java.util.List.of("1", "2", "3", "4", "5"), lines);
    }

    @Test
    void chainsCommandsWithAnd() {
        var result = exec.run("echo one && echo two");
        assertEquals(0, result.exitCode());
        var lines = result.stdout().lines().toList();
        assertEquals(java.util.List.of("one", "two"), lines);
    }

    @Test
    void shortCircuitsFailedChain() {
        var result = exec.run("false && echo unreachable");
        assertFalse(result.success());
        assertEquals("", result.stdout());
    }

    @Test
    void execsRelativeProgramInDirectory(@TempDir Path dir) throws IOException {
        var script = dir.resolve("hello.sh");
        Files.writeString(script, "#!/bin/sh\necho hi from script\n");
        Files.setPosixFilePermissions(script, PosixFilePermissions.fromString("rwxr-xr-x"));
        var result = exec.run("./hello.sh", dir);
        assertEquals(0, result.exitCode());
        assertEquals("hi from script" + System.lineSeparator(), result.stdout());
    }
}
