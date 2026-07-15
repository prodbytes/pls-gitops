package pls.cli.deploy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import pls.cli.context.PlsContext;

@ApplicationScoped
public class DeployHandler {

    @Inject
    PlsContext ctx;

    void onDeploy(@Observes DeployEvent event) {
        ctx.info("Deploy started in %s", event.dir().toAbsolutePath().normalize());
    }

}
