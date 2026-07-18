package pls.cli.files;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pls.cli.ResourceRecord;
import pls.cli.context.PlsContext;

@ApplicationScoped
public class FileScanner {

    @Inject
    PlsContext ctx;

    /**
     * Scans all files under the context dir and returns each file that has an
     * action set as a {@link ResourceRecord}, ordered by full path.
     */
    public List<ResourceRecord> scan() {
        try (Stream<Path> paths = Files.walk(ctx.getDir())) {
            return paths.filter(Files::isRegularFile)
                    .map(ResourceRecord::new)
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to scan " + ctx.getDir(), e);
        }
    }

    /** Returns the action set for the given file, or null if none applies. */

}
