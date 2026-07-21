package pls.cli.plan;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import pls.cli.Action;
import pls.cli.ActionRecord;
import pls.cli.ActionSets;
import pls.cli.ResourceKind;
import pls.cli.ResourceRecord;
import pls.cli.bins.BinPlanner;
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

    @Inject
    BinPlanner binPlanner;

    private List<ActionRecord> plan = new LinkedList<>();

    public void accept(Action action) {
        var resources = ctx.getResourceRecords();
        for (var resource : resources) {
            var kind = resource.kind();
            var optionalAction = Optional.<ActionRecord>empty();
            if (kind == ResourceKind.FILE) {
                optionalAction = filePlanner.planFor(resource, action);
            }
            if (kind == ResourceKind.BIN) {
                optionalAction = binPlanner.planFor(resource, action);
            }
            if (optionalAction.isPresent()) {
                var resourceAction = optionalAction.get();
                plan(resourceAction);
            }
        }
        var actionCount = plan.size();
        log.info("Planning fase completed with [%s] actions", actionCount);
        plan.forEach(a -> log.debug("%s", a));
    }

    public void plan(ActionRecord action) {
        hook("before", action);
        plan.add(action);
        hook("after", action);
    }

    private void hook(String hook, ActionRecord action) {
        var resource = action.subject();
        if (resource.kind() != ResourceKind.FILE) {
            return;
        }
        var fileName = resource.path().getFileName().toString();
        var firstDot = fileName.indexOf('.');
        var baseName = firstDot < 0 ? fileName : fileName.substring(0, firstDot);
        var hookFileName = "%s.%s-%s.sh".formatted(baseName, hook, action.action());
        var hookPath = resource.path().resolveSibling(hookFileName);
        for (var candidate : ctx.getResourceRecords()) {
            if (candidate.kind() == ResourceKind.FILE && candidate.path().equals(hookPath)) {
                log.debug("Hook [%s-%s] for [%s] => %s", hook, action.action(), resource, candidate);
                var hookAction = new ActionRecord(
                    Action.EXEC,
                    ActionSets.Shell,
                    candidate
                );
                plan.add(hookAction);
            }
        }
    }

    public List<ActionRecord> get() {
        return plan;
    }

}
