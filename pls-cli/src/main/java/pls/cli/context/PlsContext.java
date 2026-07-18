package pls.cli.context;

import java.nio.file.Path;
import java.util.List;

import org.jboss.logging.Logger;

import pls.cli.ResourceRecord;
import pls.cli.act.ActContext;
import pls.cli.plan.PlanContext;
import pls.cli.report.ReportContext;
import pls.cli.scan.ScanContext;

public interface PlsContext {


    default void init(){};
    /** Blocks until the context is done (e.g. the TUI exits); no-op for console. */
    default void await(){};
    
    String getGoal();
    void setGoal(String goal);
    Path getDir();
    void setDir(Path dir);
    
    List<ResourceRecord> getResourceRecords();
    void setResourceRecords(List<ResourceRecord> resourceRecords);


    ScanContext scan();
    PlanContext plan();
    ActContext act();
    ReportContext report();
}
