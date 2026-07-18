package pls.cli.plan;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pls.cli.Action;
import pls.cli.ResourceKind;
import pls.cli.ResourceRecord;
import pls.cli.files.FilePlanner;
import pls.cli.log.Logs;

@ApplicationScoped
public class Plans {

    @Inject
    FilePlanner filePlanner;    

    @Inject
    Logs log;

    public List<Action> planFor(Action action, ResourceRecord resource) {
        var kind = resource.kind();
        var result = List.<Action>of();
        if (kind == ResourceKind.FILE) {
             result = filePlanner.planFor(action, resource);
        }
        log.info("Plan for [%s] => %s", resource, result);
        return result;
    }

    
}
