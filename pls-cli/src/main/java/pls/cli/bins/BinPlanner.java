package pls.cli.bins;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import pls.cli.Action;
import pls.cli.ActionRecord;
import pls.cli.ActionSets;
import pls.cli.ResourceRecord;

@ApplicationScoped
public class BinPlanner {

    public Optional<ActionRecord> planFor(ResourceRecord resource, Action action) {
        var actionSet = ActionSets.Bin;
        if (actionSet.contains(action)) {
            var actionRecord = new ActionRecord(
                action,
                actionSet,
                resource
            );
            return Optional.of(actionRecord);
        }
        return Optional.empty();
    }

}
