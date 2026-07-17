package pls.cli.context;

import java.nio.file.Path;

import jakarta.inject.Inject;
import pls.cli.act.ActContext;
import pls.cli.classify.ClassifyContext;
import pls.cli.report.ReportContext;
import pls.cli.scan.ScanContext;

/**
 * AnyContext
 */
public abstract class AnyContext implements PlsContext {

    private String goal;
    private Path dir;

    @Inject
    ScanContext scanContext;
    
    @Inject
    ClassifyContext classifyContext;
    
    @Inject
    ActContext actContext;

    @Inject
    ReportContext reportContext;

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

    public ScanContext scan() {
        return scanContext;
    }

    public ClassifyContext classify() {
        return classifyContext;
    }

    public ActContext act() {
        return actContext;
    }

    public ReportContext report() {
        return reportContext;
    }
}
