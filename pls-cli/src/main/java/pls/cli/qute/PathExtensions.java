package pls.cli.qute;

import java.nio.file.Path;

import io.quarkus.qute.TemplateExtension;

/** Qute extension methods for {@link Path} values used in command templates. */
@TemplateExtension
public class PathExtensions {

    /** File name without directories or extensions: /a/b/c/x.y.z => x. */
    static String stem(Path path) {
        var fileName = path.getFileName().toString();
        var firstDot = fileName.indexOf('.');
        return firstDot <= 0 ? fileName : fileName.substring(0, firstDot);
    }
}
