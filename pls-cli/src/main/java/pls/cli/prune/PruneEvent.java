package pls.cli.prune;

import java.nio.file.Path;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import pls.cli.context.PlsContext;

@Dependent
public class PruneEvent {

    @Inject
    PlsContext ctx;

    public Path dir() {
        return ctx.getDir();
    }

}
