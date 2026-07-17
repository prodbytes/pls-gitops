package pls.cli.context;

import java.nio.file.Path;

import org.jboss.logging.Logger;

import pls.cli.act.ActContext;
import pls.cli.classify.ClassifyContext;
import pls.cli.report.ReportContext;
import pls.cli.scan.ScanContext;

public interface PlsContext {

    StackWalker CALLER_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    default void init(){};
    /** Blocks until the context is done (e.g. the TUI exits); no-op for console. */
    default void await(){};
    
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
                .filter(f -> !PlsContext.class.isAssignableFrom(f.getDeclaringClass()))
                .findFirst()
                .map(StackWalker.StackFrame::getClassName)
                .orElse(PlsContext.class.getName()));
    }

    void setGoal(String goal);
    void setDir(Path dir);
    String getGoal();
    Path getDir();

    ScanContext scan();
    ClassifyContext classify();
    ActContext act();
    ReportContext report();
}
