package pls.cli.files;

import java.nio.file.Path;

import jakarta.enterprise.context.ApplicationScoped;
import pls.cli.ActionSet;
import pls.cli.ActionSets;

@ApplicationScoped
public class FileClassifier {

    public ActionSet actionSetFor(Path file) {
        var name = file.getFileName().toString();
        if (name.contains(".cform.")) {
            return ActionSets.CLOUDFORMATION;
        }
        if (name.endsWith(".sh")) {
            return ActionSets.SHELL;
        }
        return null;
    }
    
}
