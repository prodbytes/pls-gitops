package pls.cli.act;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.Optional;

import io.quarkus.qute.Engine;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import pls.cli.Action;
import pls.cli.ActionRecord;
import pls.cli.context.PlsContext;
import pls.cli.exec.ExecCmd;
import pls.cli.log.Logs;

/** State of the act phase. */
@Dependent
public class ActContext {

    private static final String COMMAND_TEMPLATE = "main.qute.sh";

    @Inject
    Logs log;

    @Inject
    PlsContext ctx;

    @Inject
    ExecCmd exec;

    @Inject
    Engine engine;

    public void accept(Action action) {
        log.info("Performing %s", action);
        var plan = ctx.plan().get();
        plan.stream().forEach(ar -> accept(action, ar));
    }

    private void accept(Action action, ActionRecord ar) {
        var template = commandTemplateFor(ar);
        if (template.isEmpty()) {
            log.warn("No command found for %s", ar);
            return;
        }
        var command = render(template.get(), ar);
        ar.timeStart(Instant.now());
        var result = exec.run(command, ctx.getWorkDir());
        ar.timeEnd(Instant.now());
        ar.result(result);
    }

    /**
     * Resolves the command template for the record: the PLS_$ACTION_$ACTIONSET
     * environment variable if defined, or else the bundled resource
     * actions/$actionSet/$action/main.qute.sh.
     */
    private Optional<String> commandTemplateFor(ActionRecord ar) {
        var action = ar.action().value().toLowerCase();
        var actionSet = ar.actionSet().name().toLowerCase();
        var envVar = "PLS_%s_%s".formatted(action, actionSet).toUpperCase();
        var override = System.getenv(envVar);
        if (override != null) {
            log.debug("Using command from %s for %s", envVar, ar);
            return Optional.of(override);
        }
        var resource = "actions/%s/%s/%s".formatted(actionSet, action, COMMAND_TEMPLATE);
        try (var in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                return Optional.empty();
            }
            log.debug("Using command from %s for %s", resource, ar);
            return Optional.of(new String(in.readAllBytes()));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read command template: " + resource, e);
        }
    }

    private String render(String template, ActionRecord ar) {
        return engine.parse(template)
                .data("action", ar.action())
                .data("actionSet", ar.actionSet())
                .data("subject", ar.subject())
                .data("dir", ctx.getDir())
                .data("prefix", ctx.getPrefix())
                .data("workDir", ctx.getWorkDir())
                .data("goal", ctx.getGoal())
                .render();
    }
}
