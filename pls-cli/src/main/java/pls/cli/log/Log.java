package pls.cli.log;

import org.jboss.logging.Logger;

import pls.cli.context.PlsContext;

public interface Log {
    StackWalker CALLER_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    default void info(String format, Object... params){
        log().infof(format, params);
    }

    default void debug(String format, Object... params){
        log().debugf(format, params);
    }

    default void warn(String format, Object... params){
        log().warnf(format, params);
    }
    
    default void error(String format, Object... params){
        log().errorf(format, params);
    }

    default void debug(Throwable t, String format, Object... params){
        log().debugf(t, format, params);
    }

    default void error(Throwable t, String format, Object... params){
        log().errorf(t, format, params);
    }

    default Logger log(){
        return Logger.getLogger(caller());
    }

    default String caller(){
        return CALLER_WALKER.walk(frames -> frames
                .filter(f -> !Log.class.isAssignableFrom(f.getDeclaringClass()))
                .findFirst()
                .map(StackWalker.StackFrame::getClassName)
                .orElse(PlsContext.class.getName()));
    }
}
