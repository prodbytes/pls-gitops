package pls.cli;

import java.nio.file.Path;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import pls.cli.config.PlsConfig;
import pls.cli.context.PlsContext;
import pls.cli.deploy.DeployEvent;
import pls.cli.prune.PruneEvent;

@Command(name = "pls", mixinStandardHelpOptions = true)
public class PlsCommand implements Runnable {

    @Inject
    PlsConfig config;
    
    @Inject
    PlsContext ctx;

    @Inject
    Event<DeployEvent> deployEvent;

    @Inject
    DeployEvent deploy;

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
        ctx.info("config.tuiEnabled: %s", config.tuiEnabled().map(String::valueOf).orElse("unset"));
        ctx.setGoal(goal);
        ctx.setDir(dir);
        ctx.init();
        switch (goal) {
            case "deploy" -> deployEvent.fire(deploy);
            case "prune" -> pruneEvent.fire(prune);
            case null -> ctx.info("No goal specified, nothing to do. Try 'gitops', 'prune' or 'help' for more information.");
            default -> ctx.info("Unknown handler for goal: %s", goal);
        }

        ctx.info("Done, quit with 'q' or 'ctrl+c'!");
        ctx.await();
    }

}
