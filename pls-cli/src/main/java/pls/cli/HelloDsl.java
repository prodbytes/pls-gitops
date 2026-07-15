package pls.cli;

import dev.tamboui.toolkit.app.ToolkitApp;
import dev.tamboui.toolkit.element.Element;

import static dev.tamboui.toolkit.Toolkit.*;

public class HelloDsl extends ToolkitApp {

    @Override
    protected Element render() {
        return panel("Hello",
            text("Welcome to TamboUI DSL!").bold().cyan(),
            spacer(),
            text("Press 'q' to quit").dim()
        ).rounded();
    }
}