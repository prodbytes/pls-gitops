package pls.cli;

import jakarta.inject.Inject;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import pls.cli.context.PlsContext;

@Command(name = "main", mixinStandardHelpOptions = true)
public class MainCommand implements Runnable {

    @Inject
    PlsContext ctx;

    @Override
    public void run() {
        ctx.info("PLS CLI is running in %s mode", ctx.getClass().getSimpleName());
        ctx.init();
        ctx.info("Done!"); 
    }

}
