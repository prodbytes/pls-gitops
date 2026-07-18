package pls.cli.prune;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import pls.cli.context.PlsContext;
import pls.cli.log.Log;

@ApplicationScoped
public class PruneHandler {

    @Inject
    PlsContext ctx;

    @Inject
    Log log;

    void onPrune(@Observes PruneEvent event) {
        log.info("Prune started in %s", event.dir().toAbsolutePath().normalize());
    }

}
