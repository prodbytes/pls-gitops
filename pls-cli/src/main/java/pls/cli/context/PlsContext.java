package pls.cli.context;

import java.nio.file.Path;

import org.jboss.logging.Logger;

import pls.cli.act.ActContext;
import pls.cli.plan.PlanContext;
import pls.cli.report.ReportContext;
import pls.cli.scan.ScanContext;

public interface PlsContext {


    default void init(){};
    /** Blocks until the context is done (e.g. the TUI exits); no-op for console. */
    default void await(){};
    


    void setGoal(String goal);
    void setDir(Path dir);
    String getGoal();
    Path getDir();

    ScanContext scan();
    PlanContext classify();
    ActContext act();
    ReportContext report();
}
