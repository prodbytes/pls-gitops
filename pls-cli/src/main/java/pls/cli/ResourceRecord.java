package pls.cli;

import java.nio.file.Path;
import java.util.TreeMap;

/**
 * Key map for a single scanned resource ({"path": ..., "kind": "file"}),
 * ordered by full path.
 */
public class ResourceRecord extends TreeMap<String, String> implements Comparable<ResourceRecord> {

    public static final String KIND_FILE = "file";

    public ResourceRecord(Path path) {
        put("path", path.toString());
        put("kind", KIND_FILE);
    }

    public String path() {
        return get("path");
    }

    @Override
    public int compareTo(ResourceRecord other) {
        return path().compareTo(other.path());
    }
}
