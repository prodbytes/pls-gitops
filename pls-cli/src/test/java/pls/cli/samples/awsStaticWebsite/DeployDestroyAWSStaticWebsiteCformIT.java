package pls.cli.samples.awsStaticWebsite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;

/**
 * Deploys the aws-static-website sample to the AWS account of the current
 * credentials, verifies the website bucket exists, destroys the stack, and
 * verifies the bucket is gone.
 *
 * The *IT suffix keeps it out of plain {@code mvn test} (it creates and
 * deletes real AWS resources); run it manually like
 * {@link DeployAWSStaticWebsiteCform}.
 */
@QuarkusMainTest
class DeployDestroyAWSStaticWebsiteCformIT {

    static final String STACK_NAME = "aws-static-website";

    @Test
    void deployThenDestroy(QuarkusMainLauncher launcher) throws Exception {
        var dir = sampleDir().toString();

        var deploy = launcher.launch("deploy", dir);
        assertEquals(0, deploy.exitCode(), "deploy exited with " + deploy.exitCode());

        var bucket = bucketName();
        assertFalse(bucket.isEmpty() || "None".equals(bucket),
                "stack %s has no BucketName output".formatted(STACK_NAME));
        assertTrue(bucketExists(bucket), "bucket %s should exist after deploy".formatted(bucket));

        var destroy = launcher.launch("destroy", dir);
        assertEquals(0, destroy.exitCode(), "destroy exited with " + destroy.exitCode());

        assertFalse(bucketExists(bucket), "bucket %s should be gone after destroy".formatted(bucket));
    }

    /** The sample directory, whether run from the repo root or the pls-cli module. */
    private Path sampleDir() {
        var fromRoot = Path.of("samples/aws-static-website/cform");
        return Files.isDirectory(fromRoot) ? fromRoot : Path.of("../samples/aws-static-website/cform");
    }

    private String bucketName() throws IOException, InterruptedException {
        var result = aws("cloudformation", "describe-stacks",
                "--stack-name", STACK_NAME,
                "--query", "Stacks[0].Outputs[?OutputKey=='BucketName'].OutputValue",
                "--output", "text");
        assertEquals(0, result.exitCode(), "describe-stacks failed: " + result.output());
        return result.output().strip();
    }

    private boolean bucketExists(String bucket) throws IOException, InterruptedException {
        return aws("s3api", "head-bucket", "--bucket", bucket).exitCode() == 0;
    }

    private record AwsResult(int exitCode, String output) {
    }

    private AwsResult aws(String... args) throws IOException, InterruptedException {
        var command = new ArrayList<String>();
        command.add("aws");
        command.addAll(List.of(args));
        var process = new ProcessBuilder(command).redirectErrorStream(true).start();
        var output = new String(process.getInputStream().readAllBytes());
        var exited = process.waitFor(5, TimeUnit.MINUTES);
        assertTrue(exited, "aws command timed out: " + command);
        return new AwsResult(process.exitValue(), output);
    }
}
