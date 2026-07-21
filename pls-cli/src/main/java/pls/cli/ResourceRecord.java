package pls.cli;

import java.nio.file.Path;
import java.util.TreeMap;

/**
 * Key map for a single scanned resource ({"path": ..., "kind": "file"}),
 * ordered by full path.
 */
public class ResourceRecord extends TreeMap<String, Object> implements Comparable<ResourceRecord> {

    private ResourceKind kind;


    public ResourceRecord(Path path) {
        put("path", path);
        put("kind", ResourceKind.FILE.name());
        if (path.toFile().isFile()) {
            kind = ResourceKind.FILE;
        }
    }

    public ResourceRecord(Path path, ResourceKind kind) {
        put("path", path);
        put("kind", kind.name());
        this.kind = kind;
    }

    public ResourceKind kind() {
        return kind;
    }

    public Path path() {
        return (Path) get("path");
    }

    @Override
    public int compareTo(ResourceRecord other) {
        return path().compareTo(other.path());
    }
}
