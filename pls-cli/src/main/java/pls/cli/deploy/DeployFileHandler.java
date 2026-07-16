package pls.cli.deploy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import pls.cli.context.PlsContext;

@ApplicationScoped
public class DeployFileHandler {

    @Inject
    PlsContext ctx;

    void onDeployFile(@Observes DeployFile event) {
        ctx.info("Deploying %s with %s:%s", event.file().path(), event.actionSet(), event.action());
    }

}
