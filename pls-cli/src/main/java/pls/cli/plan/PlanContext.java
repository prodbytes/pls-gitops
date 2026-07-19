package pls.cli.plan;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import pls.cli.Action;
import pls.cli.ResourceKind;
import pls.cli.ResourceRecord;
import pls.cli.context.PlsContext;
import pls.cli.files.FilePlanner;
import pls.cli.log.Logs;

/** State of the plan phase. */
@Dependent
public class PlanContext {
    @Inject
    Logs log;

    @Inject
    PlsContext ctx;

    @Inject
    FilePlanner filePlanner;

    //TODO make plan a list of ActionRecord, retaining the ActionSet
    private Map<ResourceRecord, Action> plan = new LinkedHashMap<>();

    public void accept(Action action) {
        var resources = ctx.getResourceRecords();
        for (var resource : resources) {
        var kind = resource.kind();
        var optionalAction = Optional.<Action>empty();
        if (kind == ResourceKind.FILE) {
            optionalAction = filePlanner.planFor(resource,action);
        }
        if (optionalAction.isPresent()) {
            var resourceAction = optionalAction.get();
            plan(resource, resourceAction);
        }
        }
        var actionCount = plan.size();
        log.info("Planning fase completed with [%s] actions", actionCount);
        plan.forEach((r,a) -> log.debug("%s => %s",a,r));
    }

    public void plan(ResourceRecord resource, Action action) {
        hook("before", resource, action);
        plan.put(resource, action);
        hook("after", resource, action);
    }

    private void hook(String hook, ResourceRecord resource, Action action) {
        if (resource.kind() != ResourceKind.FILE) {
            return;
        }
        var fileName = resource.path().getFileName().toString();
        var firstDot = fileName.indexOf('.');
        var baseName = firstDot < 0 ? fileName : fileName.substring(0, firstDot);
        var hookFileName = "%s.%s-%s.sh".formatted(baseName, hook, action);
        var hookPath = resource.path().resolveSibling(hookFileName);
        for (var candidate : ctx.getResourceRecords()) {
            if (candidate.kind() == ResourceKind.FILE && candidate.path().equals(hookPath)) {
                log.debug("Hook [%s-%s] for [%s] => %s", hook, action, resource, candidate);
                plan.put(candidate, Action.EXEC);
            }
        }
    }
    
}
