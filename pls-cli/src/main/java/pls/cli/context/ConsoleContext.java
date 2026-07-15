package pls.cli.context;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;

// @Typed keeps this bean from matching PlsContext injection points,
// which would be ambiguous with the ContextProducer bean.
@Typed(ConsoleContext.class)
@ApplicationScoped
public class ConsoleContext extends AnyContext {

}
