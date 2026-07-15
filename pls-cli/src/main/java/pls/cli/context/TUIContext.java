package pls.cli.context;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import pls.cli.tui.PlsTUI;

// @Typed keeps this bean from matching PlsContext injection points,
// which would be ambiguous with the ContextProducer bean.
@Typed(TUIContext.class)
@ApplicationScoped
public class TUIContext implements PlsContext {
    @Inject
    PlsTUI tui;

    @Override
    public void init() {
        try {
            tui.run();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
