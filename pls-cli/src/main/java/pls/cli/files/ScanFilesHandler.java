package pls.cli.files;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import pls.cli.ActionSets;
import pls.cli.context.PlsContext;
import pls.cli.log.Logs;

@ApplicationScoped
public class ScanFilesHandler {

    @Inject
    Logs log;
    
    @Inject
    PlsContext ctx;

    @Inject
    FileScanner fileScanner;


    void onDeploy(@Observes ScanFilesEvent event) {
        log.debug("Scan files started in %s", event.dir().toAbsolutePath().normalize());
        var fileResources = fileScanner.scan();
        ctx.setResourceRecords(fileResources);
    }

}
