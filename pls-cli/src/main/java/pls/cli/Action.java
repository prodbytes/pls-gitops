package pls.cli;

/** Type alias for an action, which is a simple string identifier. */
public record Action(String value) {

    public static final Action DEPLOY = new Action("deploy");
    public static final Action PRUNE = new Action("prune");

    @Override
    public String toString() {
        return value;
    }
}
