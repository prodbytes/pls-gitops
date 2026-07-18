package pls.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;
import pls.cli.config.PlsConfig;
import pls.cli.context.PlsContext;
import pls.cli.files.ScanFilesEvent;
import pls.cli.log.Logs;
import pls.cli.prune.PruneEvent;

@Command(name = "pls", mixinStandardHelpOptions = true)
public class PlsCommand implements Runnable {

    @Inject
    PlsConfig config;
    
    @Inject
    PlsContext ctx;

    @Inject
    Logs log;

    @Inject
    Event<ScanFilesEvent> deployEvent;

    @Inject
    ScanFilesEvent deploy;

    @Inject
    Event<PruneEvent> pruneEvent;

    @Inject
    PruneEvent prune;

    @Spec
    CommandSpec spec;

    @Parameters(index = "0", paramLabel = "goal", arity = "0..1", defaultValue = "help",
            description = "The goal to accomplish: help, deploy, destroy, prune, or gitops (deploy then prune)")
    String goal;

    @Parameters(index = "1", paramLabel = "dir", arity = "0..1", defaultValue = ".", description = "Target directory (defaults to current directory)")
    Path dir;

    @Override
    public void run() {
        if ("help".equals(goal)) {
            spec.commandLine().usage(spec.commandLine().getOut());
            return;
        }

        log.info("pls... running. Press 'q' or 'ctrl+c' to exit.");
        log.info("goal: %s" ,goal); 
        log.info("dir: %s", dir.toAbsolutePath().normalize());
        log.debug("config.tuiEnabled: %s", config.tuiEnabled().map(String::valueOf).orElse("unset"));

        if (!Files.isDirectory(dir)) {
            log.info("Directory does not exist: %s", dir.toAbsolutePath().normalize());
            return;
        }

        ctx.setGoal(goal);
        ctx.setDir(dir);
        ctx.init();
        
        var actions = plan(new Goal(goal));
        for (var action : actions) {
            spar(action);    
        }
        

        log.info("Done, quit with 'q' or 'ctrl+c'!");
        ctx.await();
    }

    private List<Action> plan(Goal goal) {
        return switch (goal.value()) {
            case "deploy" -> List.of(Action.DEPLOY);
            case "destroy" -> List.of(Action.DESTROY);
            case "prune" -> List.of(Action.PRUNE);
            case "gitops" -> List.of(Action.DEPLOY, Action.PRUNE);
            default -> List.of();
        };
    }

    private void spar(Action action) {
        scan(action);
        plan(action);
        act(action);
        report(action);
    }

    private void scan(Action action) {
        log.info("🔎 Scanning on [%s]", action);
        ctx.scan().accept(action);
    }

    private void plan(Action action) {
        log.info("🧐 Planning on [%s]", action);
        ctx.plan().accept(action);
    }

    private void act(Action action) {
        log.info("⚡ Acting on [%s]", action);
    }

    private void report(Action action) {
        log.info("📝 Reporting on [%s]", action);
    }

}
