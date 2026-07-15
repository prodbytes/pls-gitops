package pls.cli.context;

import java.util.Locale;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import pls.cli.config.PlsConfig;

@ApplicationScoped
public class ContextProducer {
    @Inject
    PlsConfig config;

    @Inject
    Instance<TUIContext> tuiContext;

    @Inject
    Instance<ConsoleContext> consoleContext;

    @Produces
    @ApplicationScoped
    public PlsContext context() {
        // Explicit config wins; only fall back to desktop detection when unset.
        var isTUI = isTUI();
        if (isTUI) {
            return tuiContext.get();
        }else{
            return consoleContext.get();
        }
    }

    public boolean isTUI() {
       return config.tuiEnabled().orElseGet(ContextProducer::isDesktop);
    }

	public static boolean isDesktop() {
        var os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("windows")) {
            return true;
        }
        if (os.contains("mac") || os.contains("darwin")) {
            return true;
        }
        if (os.contains("linux")) {
            // Only a desktop if a graphical session is actually reachable,
            // i.e. X11 or Wayland; headless CI/CD boxes have neither.
            return hasEnv("WAYLAND_DISPLAY") || hasEnv("DISPLAY");
        }
        return false;
    }

    private static boolean hasEnv(String name) {
        var value = System.getenv(name);
        return value != null && !value.isBlank();
    }

}
