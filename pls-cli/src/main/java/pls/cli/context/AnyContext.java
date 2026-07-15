package pls.cli.context;

import java.nio.file.Path;

/**
 * AnyContext
 */
public abstract class AnyContext implements PlsContext {

    private String goal;
    private Path dir;

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Path getDir() {
        return dir;
    }

    public void setDir(Path dir) {
        this.dir = dir;
    }

}
