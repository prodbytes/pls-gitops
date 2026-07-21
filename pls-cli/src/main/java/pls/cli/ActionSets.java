package pls.cli;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;


public enum ActionSets {
    Cloudformation(Set.of(Action.DEPLOY, Action.DESTROY)),
    Shell(Set.of(Action.EXEC)),
    Bin(Set.of(Action.VERSION));

    Set<Action> actions;

    ActionSets(Set<Action> actions){
        this.actions = actions;
    }

    public boolean contains(Action action){
        return actions.contains(action);
    }

    
    
}
