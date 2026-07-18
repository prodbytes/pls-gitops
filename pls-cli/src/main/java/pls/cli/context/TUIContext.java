package pls.cli.context;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import pls.cli.tui.PlsTUI;

// @Typed keeps this bean from matching PlsContext injection points,
// which would be ambiguous with the ContextProducer bean.
@Typed(TUIContext.class)
@ApplicationScoped
public class TUIContext extends AnyContext {
    @Inject
    PlsTUI tui;

    private Thread tuiThread;

    @Override
    public void init() {
        // Run the blocking TUI event loop on its own thread so goal events
        // fire on the main thread while the TUI renders their log messages.
        tuiThread = Thread.ofPlatform().name("pls-tui").start(() -> {
            try {
                tui.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void await() {
        if (tuiThread == null) {
            return;
        }
        try {
            tuiThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
