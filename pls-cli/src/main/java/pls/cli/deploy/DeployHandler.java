package pls.cli.deploy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import pls.cli.Action;
import pls.cli.ActionSets;
import pls.cli.context.PlsContext;
import pls.cli.files.FileScanner;

@ApplicationScoped
public class DeployHandler {

    private static final Action DEPLOY = new Action("deploy");

    @Inject
    PlsContext ctx;

    @Inject
    FileScanner fileScanner;

    @Inject
    ActionSets actionSets;

    @Inject
    Event<DeployFile> deployFile;

    void onDeploy(@Observes DeployEvent event) {
        ctx.info("Deploy started in %s", event.dir().toAbsolutePath().normalize());
        fileScanner.scan().forEach((file, actionSet) -> {
            if (actionSets.actionsFor(actionSet).contains(DEPLOY)) {
                deployFile.fire(new DeployFile(file, actionSet, DEPLOY));
            }
        });
    }

}
