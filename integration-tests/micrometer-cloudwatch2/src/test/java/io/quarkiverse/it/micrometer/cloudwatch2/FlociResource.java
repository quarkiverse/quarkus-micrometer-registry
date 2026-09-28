package io.quarkiverse.it.micrometer.cloudwatch2;

import java.net.URI;
import java.util.Map;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;

/**
 * Starts Floci (https://github.com/floci-io/floci), a free AWS emulator, and points the default
 * CloudWatchAsyncClient created by the extension at it.
 * <p>
 * Floci accepts any credentials: the AWS SDK reads them from the AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY
 * environment variables set by the surefire and failsafe configuration.
 */
public class FlociResource implements QuarkusTestResourceLifecycleManager {

    static final DockerImageName IMAGE = DockerImageName.parse("floci/floci:2.1.0");
    static final int PORT = 4566;
    static final String REGION = "us-east-1";
    static final String ACCESS_KEY = "test";
    static final String SECRET_KEY = "test";

    static GenericContainer<?> floci;

    @Override
    public Map<String, String> start() {
        // Configured through setters: the fluent with*() methods trigger a false "resource leak" warning
        floci = new GenericContainer<>(IMAGE);
        floci.addExposedPort(PORT);
        floci.setWaitStrategy(Wait.forHttp("/_localstack/health").forPort(PORT));
        floci.start();

        return Map.of(
                "quarkus.micrometer.export.cloudwatch.region", REGION,
                "quarkus.micrometer.export.cloudwatch.endpoint-override", endpoint().toString());
    }

    @Override
    public void stop() {
        if (floci != null) {
            floci.close();
        }
    }

    static URI endpoint() {
        return URI.create("http://" + floci.getHost() + ":" + floci.getMappedPort(PORT));
    }

    static CloudWatchClient cloudWatchClient() {
        return CloudWatchClient.builder()
                .endpointOverride(endpoint())
                .region(Region.of(REGION))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
                .build();
    }
}
