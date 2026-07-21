package pls.cli.bins;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import pls.cli.context.PlsContext;
import pls.cli.log.Logs;

@ApplicationScoped
public class ScanBinsHandler {

    @Inject
    Logs log;

    @Inject
    PlsContext ctx;

    @Inject
    BinScanner binScanner;

    void onScanBins(@Observes ScanBinsEvent event) {
        log.debug("Scan bins started for %s", event.names());
        var binResources = binScanner.scan(event.names());
        ctx.setResourceRecords(binResources);
    }

}
