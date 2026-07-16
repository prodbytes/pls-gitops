package pls.cli;

import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;

/** Known action sets and the actions each one provides. */
@ApplicationScoped
public class ActionSets {

    public static final ActionSet CLOUDFORMATION = new ActionSet("cloudformation");
    public static final ActionSet SHELL = new ActionSet("shell");

    private static final Map<ActionSet, List<Action>> ACTIONS = Map.of(
            CLOUDFORMATION, List.of(new Action("deploy"), new Action("destroy")),
            SHELL, List.of(new Action("execute")));

    /** Returns the actions available for the given action set, or an empty list if unknown. */
    public List<Action> actionsFor(ActionSet actionSet) {
        return ACTIONS.getOrDefault(actionSet, List.of());
    }
}
