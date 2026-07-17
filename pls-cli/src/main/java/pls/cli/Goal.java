package pls.cli;

public record Goal (String value) {
    @Override
    public String toString() {
        return value;
    }
}