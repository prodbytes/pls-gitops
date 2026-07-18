package pls.cli.context;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import pls.cli.log.ConsoleLog;

// @Typed keeps this bean from matching PlsContext injection points,
// which would be ambiguous with the ContextProducer bean.
@Typed(ConsoleContext.class)
@ApplicationScoped
public class ConsoleContext extends AnyContext {
    @Inject
    ConsoleLog log;

}
