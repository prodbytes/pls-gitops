package pls.cli.files;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import pls.cli.Action;
import pls.cli.ActionSets;
import pls.cli.context.PlsContext;
import pls.cli.deploy.DeployFile;
import pls.cli.log.Log;

@ApplicationScoped
public class ScanFilesHandler {

    private static final Action DEPLOY = new Action("deploy");

    @Inject
    Log log;
    
    @Inject
    PlsContext ctx;

    @Inject
    FileScanner fileScanner;

    @Inject
    ActionSets actionSets;

    @Inject
    Event<DeployFile> deployFile;

    void onDeploy(@Observes ScanFilesEvent event) {
        log.info("Scan files started in %s", event.dir().toAbsolutePath().normalize());
        var fileResources = fileScanner.scan();
        ctx.scan().setResourceRecords(fileResources);
    }

}
