package pls.cli.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "pls")
@StaticInitSafe
public interface PlsConfig {
    Optional<Boolean> tuiEnabled();
    
}
