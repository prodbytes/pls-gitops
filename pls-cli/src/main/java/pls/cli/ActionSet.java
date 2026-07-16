package pls.cli;

/** Type alias for an action set, which is a simple string identifier. */
public record ActionSet(String value) {

    @Override
    public String toString() {
        return value;
    }
}
