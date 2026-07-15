package pls.cli.context;

import java.nio.file.Path;

import io.quarkus.logging.Log;

public interface PlsContext {

    default void init(){};
    /** Blocks until the context is done (e.g. the TUI exits); no-op for console. */
    default void await(){};
    default void info(String format, Object... params){
        Log.infof(format, params);
    }
    void setGoal(String goal);
    void setDir(Path dir);
    String getGoal();
    Path getDir();
}
