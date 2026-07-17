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
import pls.cli.event.PlsEvent;
import pls.cli.files.ScanFilesEvent;

/** State of the scan phase: the resources found in the context dir. */
@Dependent
public class ScanContext {
    
    @Inject
    Event<PlsEvent> eventBus;

    @Inject
    Instance<ScanFilesEvent> scanFilesEvent;

    private List<ResourceRecord> resourceRecords = List.of();

    public List<ResourceRecord> getResourceRecords() {
        return resourceRecords;
    }

    public void setResourceRecords(List<ResourceRecord> resourceRecords) {
        this.resourceRecords = resourceRecords;
    }

    public void accept(Action action) {
        // Get events for action in separate method, so that we can test it in isolation.
        var events = getEventsForAction(action);
        // fire them using cdi
        events.forEach(eventBus::fire);
        //TODO: Extract logging context to a separate class structure to avoid cyclic incjection
        Log.infof("Scan completed for action %s, found %d resources", action.value(), resourceRecords.size());   
    }

    private List<PlsEvent> getEventsForAction(Action action) {
        switch (action.value()) {
            case "deploy":
                return List.of(scanFilesEvent.get());
            case "prune":
                return List.of(); // TODO: implement prune events
            default:
                throw new IllegalArgumentException("Unknown action: " + action);
        }
    }
}
