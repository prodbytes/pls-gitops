package pls.cli.context;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;
import pls.cli.Action;
import pls.cli.ResourceRecord;
import pls.cli.act.ActContext;
import pls.cli.plan.PlanContext;
import pls.cli.report.ReportContext;
import pls.cli.scan.ScanContext;

/**
 * AnyContext
 */
public abstract class AnyContext implements PlsContext {

    @Inject
    ScanContext scanContext;
    
    @Inject
    PlanContext classifyContext;
    
    @Inject
    ActContext actContext;

    @Inject
    ReportContext reportContext;

    private String goal;
    private Path dir;
    private String prefix = "";

    //TODO: Consider moving this list to ScanContext
    private List<ResourceRecord> resourceRecords = List.of();


    public List<ResourceRecord> getResourceRecords() {
        return resourceRecords;
    }

    public void setResourceRecords(List<ResourceRecord> resourceRecords) {
        this.resourceRecords = resourceRecords;
    }


    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Path getDir() {
        return dir();
    }

    public Path dir() {
        return dir;
    }

    public void setDir(Path dir) {
        this.dir = dir;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? "" : prefix;
    }

    public ScanContext scan() {
        return scanContext;
    }

    public PlanContext plan() {
        return classifyContext;
    }

    public ActContext act() {
        return actContext;
    }

    public ReportContext report() {
        return reportContext;
    }
}
