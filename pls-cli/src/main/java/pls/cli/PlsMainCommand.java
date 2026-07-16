package pls.cli;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import picocli.CommandLine;

@ApplicationScoped
public class PlsMainCommand implements QuarkusApplication{
    
    @Inject
    CommandLine.IFactory factory;

    @Inject
    @TopCommand
    PlsCommand command;

    @Override
    public int run(String... args) {
        return new CommandLine(command, factory).execute(args);
    }

}
