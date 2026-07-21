package pls.cli.bins;

import java.util.List;

import jakarta.enterprise.context.Dependent;
import pls.cli.event.PlsEvent;

@Dependent
public class ScanBinsEvent implements PlsEvent {

    /** The tool binaries the version goal reports on. */
    public List<String> names() {
        return List.of("aws", "terraform");
    }

}
