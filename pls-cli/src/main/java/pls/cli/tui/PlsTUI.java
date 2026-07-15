package pls.cli.tui;

import dev.tamboui.toolkit.app.ToolkitApp;
import dev.tamboui.toolkit.element.Element;
import jakarta.enterprise.context.ApplicationScoped;

import static dev.tamboui.toolkit.Toolkit.*;

@ApplicationScoped
public class PlsTUI extends ToolkitApp {

    @Override
    protected Element render() {
        return panel("pls-gitops",
            text("Welcome!").bold().cyan(),
            spacer(),
            text("Press 'q' to quit").dim()
        ).rounded();
    }
}