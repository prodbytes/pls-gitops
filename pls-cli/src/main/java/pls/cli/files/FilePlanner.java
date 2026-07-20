package pls.cli.files;

import java.lang.classfile.ClassFile.Option;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pls.cli.Action;
import pls.cli.ActionRecord;
import pls.cli.ActionSets;
import pls.cli.ResourceRecord;
import pls.cli.context.PlsContext;

@ApplicationScoped
public class FilePlanner {

    @Inject
    PlsContext ctx;

    

    public Optional<ActionRecord> planFor(ResourceRecord resource, Action action) {
        var file = resource.path();
        var name = file.getFileName().toString();
        if (name.contains(".cform.")) {
            var actionSet = ActionSets.Cloudformation;
            if(actionSet.contains(action)){
                var actionRecord = new ActionRecord(
                    action,
                    actionSet,
                    resource
                );
                return Optional.of(actionRecord);
            }
        }
        return Optional.empty();
    }
    
}
