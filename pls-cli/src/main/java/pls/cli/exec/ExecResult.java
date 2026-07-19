package pls.cli.exec;

public record ExecResult(int exitCode, String stdout, String stderr) {

    public boolean success() {
        return exitCode == 0;
    }
}
