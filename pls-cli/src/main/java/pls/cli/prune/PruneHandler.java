package pls.cli.prune;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import pls.cli.context.PlsContext;

@ApplicationScoped
public class PruneHandler {

    @Inject
    PlsContext ctx;

    void onPrune(@Observes PruneEvent event) {
        ctx.info("Prune started in %s", event.dir().toAbsolutePath().normalize());
    }

}
