package pls.cli.deploy;

import pls.cli.Action;
import pls.cli.ActionSet;
import pls.cli.ResourceRecord;

/** Event payload for a single file to be deployed with the given action. */
public record DeployFile(ResourceRecord file, ActionSet actionSet, Action action) {
}
