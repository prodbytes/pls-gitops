package pls.cli.qute;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.quarkus.qute.Engine;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import pls.cli.ResourceRecord;

@QuarkusTest
class PathExtensionsTest {

    @Inject
    Engine engine;

    @Test
    void stemStripsDirectoriesAndExtensions() {
        var rendered = engine.parse("{path.stem}")
                .data("path", Path.of("/a/b/c/x.y.z"))
                .render();
        assertEquals("x", rendered);
    }

    @Test
    void stemKeepsExtensionlessName() {
        var rendered = engine.parse("{path.stem}")
                .data("path", Path.of("/a/b/Makefile"))
                .render();
        assertEquals("Makefile", rendered);
    }

    @Test
    void fileNameResolvesThroughSubjectRecord() {
        var subject = new ResourceRecord(Path.of("/tmp/stacks/website.cform.yaml"));
        var rendered = engine.parse("--template-file {subject.path.fileName}")
                .data("subject", subject)
                .render();
        assertEquals("--template-file website.cform.yaml", rendered);
    }

    @Test
    void stemResolvesThroughSubjectRecord() {
        var subject = new ResourceRecord(Path.of("/tmp/stacks/website.cform.yaml"));
        var rendered = engine.parse("--stack-name {subject.path.stem}")
                .data("subject", subject)
                .render();
        assertEquals("--stack-name website", rendered);
    }
}
