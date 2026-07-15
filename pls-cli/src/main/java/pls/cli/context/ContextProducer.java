package pls.cli.context;

import java.util.Locale;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class ContextProducer {

    @Inject
    Instance<TUIContext> tuiContext;

    @Inject
    Instance<ConsoleContext> consoleContext;

    @Produces
    @ApplicationScoped
    public PlsContext context() {
        return isDesktop() ? tuiContext.get() : consoleContext.get();
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
