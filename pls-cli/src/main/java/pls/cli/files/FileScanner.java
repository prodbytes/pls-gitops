package pls.cli.files;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pls.cli.ActionSet;
import pls.cli.ActionSets;
import pls.cli.ResourceRecord;
import pls.cli.context.PlsContext;

@ApplicationScoped
public class FileScanner {

    @Inject
    PlsContext ctx;

    /**
     * Scans all files under the context dir and maps each file that has an
     * action set (as a {@link ResourceRecord}) to its action set, ordered by
     * full path.
     */
    public SortedMap<ResourceRecord, ActionSet> scan() {
        var result = new TreeMap<ResourceRecord, ActionSet>();
        try (Stream<Path> paths = Files.walk(ctx.getDir())) {
            paths.filter(Files::isRegularFile).forEach(file -> {
                var actionSet = actionSetFor(file);
                if (actionSet != null) {
                    result.put(new ResourceRecord(file), actionSet);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to scan " + ctx.getDir(), e);
        }
        return result;
    }

    /** Returns the action set for the given file, or null if none applies. */
    public ActionSet actionSetFor(Path file) {
        var name = file.getFileName().toString();
        if (name.contains(".cform.")) {
            return ActionSets.CLOUDFORMATION;
        }
        if (name.endsWith(".sh")) {
            return ActionSets.SHELL;
        }
        return null;
    }
}
