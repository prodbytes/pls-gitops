package pls.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import pls.cli.config.PlsConfig;
import pls.cli.context.PlsContext;
import pls.cli.files.ScanFilesEvent;
import pls.cli.prune.PruneEvent;

@Command(name = "pls", mixinStandardHelpOptions = true)
public class PlsCommand implements Runnable {

    @Inject
    PlsConfig config;
    
    @Inject
    PlsContext ctx;

    @Inject
    Event<ScanFilesEvent> deployEvent;

    @Inject
    ScanFilesEvent deploy;

    @Inject
    Event<PruneEvent> pruneEvent;

    @Inject
    PruneEvent prune;

    @Parameters(index = "0", paramLabel = "goal", arity = "0..1", description = "The goal to accomplish (e.g. gitops)")
    String goal;

    @Parameters(index = "1", paramLabel = "dir", arity = "0..1", defaultValue = ".", description = "Target directory (defaults to current directory)")
    Path dir;

    @Override
    public void run() {
        ctx.info("pls... running. Press 'q' or 'ctrl+c' to exit.");
        ctx.info("goal: %s" ,goal); 
        ctx.info("dir: %s", dir.toAbsolutePath().normalize());
        ctx.debug("config.tuiEnabled: %s", config.tuiEnabled().map(String::valueOf).orElse("unset"));

        if (!Files.isDirectory(dir)) {
            ctx.info("Directory does not exist: %s", dir.toAbsolutePath().normalize());
            return;
        }

        ctx.setGoal(goal);
        ctx.setDir(dir);
        ctx.init();
        
        var actions = plan(new Goal(goal));
        for (var action : actions) {
            scar(action);    
        }
        

        ctx.info("Done, quit with 'q' or 'ctrl+c'!");
        ctx.await();
    }

    private List<Action> plan(Goal goal) {
        return switch (goal.value()) {
            case "deploy" -> List.of(Action.DEPLOY);
            case "prune" -> List.of(Action.PRUNE);
            case "gitops" -> List.of(Action.DEPLOY, Action.PRUNE);
            default -> List.of();
        };
    }

    private void scar(Action action) {
        scan(action);
        classify(action);
        act(action);
        report(action);
    }

    private void scan(Action action) {
        ctx.info("🔎 Scanning on [%s]", action);
        ctx.scan().accept(action);
    }

    private void classify(Action action) {
        ctx.info("🧐 Classifying on [%s]", action);
    }

    private void act(Action action) {
        ctx.info("⚡ Acting on [%s]", action);
    }

    private void report(Action action) {
        ctx.info("📝 Reporting on [%s]", action);
    }

}
