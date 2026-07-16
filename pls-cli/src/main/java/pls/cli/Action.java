package pls.cli;

/** Type alias for an action, which is a simple string identifier. */
public record Action(String value) {

    @Override
    public String toString() {
        return value;
    }
}
