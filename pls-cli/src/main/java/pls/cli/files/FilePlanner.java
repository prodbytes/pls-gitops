package pls.cli.files;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import pls.cli.Action;
import pls.cli.ActionSets;
import pls.cli.ResourceRecord;

@ApplicationScoped
public class FilePlanner {

    public List<Action> planFor(Action action, ResourceRecord resource) {
        var file = resource.path();
        var name = file.getFileName().toString();
        var plan = new ArrayList<Action>();
        if (name.contains(".cform.")) {
            var actionSet = ActionSets.CLOUDFORMATION;
            actionSet
                .stream()
                .filter(a -> a.equals(action))
                .forEach(plan::add);
        }
        return plan;
    }
    
}
