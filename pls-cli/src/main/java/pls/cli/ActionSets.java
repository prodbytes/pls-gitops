package pls.cli;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;

/** Known action sets and the actions each one provides. */
@ApplicationScoped
public class ActionSets {

    public static final Set<Action> CLOUDFORMATION = Set.of(Action.DEPLOY, Action.DESTROY);
    public static final Set<Action> SHELL = Set.of(Action.EXEC);

}
