package pls.cli;

import java.time.Instant;

import pls.cli.exec.ExecResult;

/** A planned action on a subject resource, holding the execution outcome once acted upon. */
public class ActionRecord {

    private final Action action;
    private final ActionSets actionSet;
    private final ResourceRecord subject;

    private ExecResult result;
    private Instant timeStart;
    private Instant timeEnd;

    public ActionRecord(Action action, ActionSets actionSet, ResourceRecord subject) {
        this.action = action;
        this.actionSet = actionSet;
        this.subject = subject;
    }

    public Action action() {
        return action;
    }

    public ActionSets actionSet() {
        return actionSet;
    }

    public ResourceRecord subject() {
        return subject;
    }

    public ExecResult result() {
        return result;
    }

    public void result(ExecResult result) {
        this.result = result;
    }

    public Instant timeStart() {
        return timeStart;
    }

    public void timeStart(Instant timeStart) {
        this.timeStart = timeStart;
    }

    public Instant timeEnd() {
        return timeEnd;
    }

    public void timeEnd(Instant timeEnd) {
        this.timeEnd = timeEnd;
    }

    @Override
    public String toString() {
        return "ActionRecord[action=%s, actionSet=%s, subject=%s]".formatted(action, actionSet, subject);
    }
}
