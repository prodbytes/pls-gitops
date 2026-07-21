package pls.cli.bins;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import pls.cli.ResourceKind;
import pls.cli.ResourceRecord;

@ApplicationScoped
public class BinScanner {

    /**
     * Finds each named binary on the PATH and returns it as a
     * {@link ResourceRecord} of kind BIN; names not found are skipped.
     */
    public List<ResourceRecord> scan(List<String> names) {
        var records = new LinkedList<ResourceRecord>();
        for (var name : names) {
            find(name).ifPresent(bin -> records.add(new ResourceRecord(bin, ResourceKind.BIN)));
        }
        return records;
    }

    private Optional<Path> find(String name) {
        var path = System.getenv("PATH");
        if (path == null) {
            return Optional.empty();
        }
        for (var dir : path.split(File.pathSeparator)) {
            if (dir.isBlank()) {
                continue;
            }
            var candidate = Path.of(dir).resolve(name);
            if (Files.isRegularFile(candidate) && Files.isExecutable(candidate)) {
                return Optional.of(candidate);
            }
        }
        return Optional.empty();
    }
}
