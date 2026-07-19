package pls.cli.exec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

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
    void separatesStderrFromStdout() {
        var result = exec.run("echo out; echo err >&2");
        assertEquals("out" + System.lineSeparator(), result.stdout());
        assertEquals("err" + System.lineSeparator(), result.stderr());
    }

    @Test
    void reportsFailureExitCode() {
        var result = exec.run("echo oops >&2; exit 3");
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
}
