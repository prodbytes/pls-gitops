package pls.cli.files;

import java.nio.file.Path;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import pls.cli.context.PlsContext;
import pls.cli.event.PlsEvent;

@Dependent
public class ScanFilesEvent implements PlsEvent {

    @Inject
    PlsContext ctx;

    public Path dir() {
        return ctx.getWorkDir();
    }

}
