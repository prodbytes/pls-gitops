package pls.cli.deploy;

import java.nio.file.Path;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import pls.cli.context.PlsContext;

@Dependent
public class DeployEvent {

    @Inject
    PlsContext ctx;

    public Path dir() {
        return ctx.getDir();
    }

}
