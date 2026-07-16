package pls.cli;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain(name="pls-main")
public class PlsMain  {
    public static void main(String... args) {
        Quarkus.run(PlsMainCommand.class, args);
    }
}
