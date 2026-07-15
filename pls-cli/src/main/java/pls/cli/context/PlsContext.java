package pls.cli.context;

import io.quarkus.logging.Log;

public interface PlsContext {

    default void init(){};
    default void info(String format, Object... params){
        Log.infof(format, params);
    }    
}
