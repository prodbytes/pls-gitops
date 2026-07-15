package pls.cli;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import picocli.CommandLine;

@QuarkusMain
public class PlsMain implements QuarkusApplication {

    @Inject
    CommandLine.IFactory factory;

    @Inject
    @TopCommand
    PlsCommand command;

    public static void main(String... args) {
        Quarkus.run(PlsMain.class, args);
    }

    @Override
    public int run(String... args) {
        return new CommandLine(command, factory).execute(args);
    }

}
