package pls.cli.scan;

import java.util.List;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import pls.cli.Action;
import pls.cli.ResourceRecord;
import pls.cli.context.PlsContext;
import pls.cli.bins.ScanBinsEvent;
import pls.cli.event.PlsEvent;
import pls.cli.files.ScanFilesEvent;
import pls.cli.log.Logs;

/** State of the scan phase: the resources found in the context dir. */
@Dependent
public class ScanContext {
    
    @Inject
    Event<PlsEvent> eventBus;

    @Inject
    Instance<ScanFilesEvent> scanFilesEvent;

    @Inject
    Instance<ScanBinsEvent> scanBinsEvent;

    @Inject
    PlsContext ctx;

    @Inject
    Logs log;

    public void accept(Action action) {
        // Get events for action in separate method, so that we can test it in isolation.
        var events = getEventsForAction(action);
        // fire them using cdi
        events.forEach(eventBus::fire);
        //TODO: Extract logging context to a separate class structure to avoid cyclic incjection
        log.info("Scan completed for action %s, found %d resources", action.value(), ctx.getResourceRecords().size()); 
        ctx.getResourceRecords().forEach(x -> log.debug("%s",x)); 
    }

    private List<PlsEvent> getEventsForAction(Action action) {
        switch (action.value()) {
            case "deploy":
            case "destroy":
                return List.of(scanFilesEvent.get());
            case "prune":
                return List.of(); // TODO: implement prune events
            case "version":
                return List.of(scanBinsEvent.get());
            default:
                throw new IllegalArgumentException("Unknown action: " + action);
        }
    }
}
