package pls.cli.tui;

import dev.tamboui.toolkit.app.ToolkitApp;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.elements.Panel;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayDeque;
import java.util.Deque;

import static dev.tamboui.toolkit.Toolkit.*;

@ApplicationScoped
public class PlsTUI extends ToolkitApp {

    private static final int LOG_PANEL_HEIGHT = 10;
    // Panel borders take 2 lines, so this is the visible message capacity.
    private static final int LOG_CAPACITY = LOG_PANEL_HEIGHT - 2;

    private final Deque<String> logMessages = new ArrayDeque<>();

    @Override
    protected Element render() {
        return column(
            resourcesPanel().fill(),
            logPanel().length(LOG_PANEL_HEIGHT)
        );
    }

    private Panel resourcesPanel() {
        return panel("pls-gitops",
            text("Welcome!").bold().cyan(),
            spacer(),
            text("Press 'q' to quit").dim()
        ).rounded();
    }

    private Panel logPanel() {
        Element[] lines;
        synchronized (logMessages) {
            lines = logMessages.stream()
                .map(message -> text(message).length(1))
                .toArray(Element[]::new);
        }
        return panel("Log", column(lines)).rounded();
    }

    /**
     * Adds a message to the log panel, shown on the next frame.
     * Only the most recent messages that fit the panel are kept.
     */
    public void info(String message) {
        synchronized (logMessages) {
            logMessages.addLast(message);
            while (logMessages.size() > LOG_CAPACITY) {
                logMessages.removeFirst();
            }
        }
    }
}
