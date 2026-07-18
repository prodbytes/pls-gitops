package pls.cli.plan;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import pls.cli.Action;
import pls.cli.ResourceRecord;
import pls.cli.context.PlsContext;
import pls.cli.log.Logs;

/** State of the plan phase. */
@Dependent
public class PlanContext {
    @Inject
    Logs log;

    @Inject
    PlsContext ctx;

    @Inject
    Plans plans;

    public void accept(Action action) {
        var resources = ctx.getResourceRecords();
        Map<ResourceRecord, List<Action>> plan = new HashMap<>();
        for (var resource : resources) {
            var actions = plans.planFor(action, resource);
            if (! actions.isEmpty()) {
                plan.put(resource, actions);
            }
        }
        var actionCount = plan.values()
            .stream()
            .map(Collection::size)
            .reduce(0, Integer::sum);
        log.info("Planning fase completed with [%s] actions", actionCount);
    }
    
}
