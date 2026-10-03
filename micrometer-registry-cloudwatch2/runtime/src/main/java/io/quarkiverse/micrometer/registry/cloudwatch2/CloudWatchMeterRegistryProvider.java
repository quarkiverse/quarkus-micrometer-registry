package io.quarkiverse.micrometer.registry.cloudwatch2;

import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchNamingConvention;
import io.quarkiverse.micrometer.registry.cloudwatch2.CloudWatchConfig.CloudWatchRuntimeConfig;
import io.quarkus.arc.DefaultBean;
import io.quarkus.runtime.configuration.ConfigurationException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClientBuilder;

/**
 * @see ConditionalRegistryProducer
 */
@Singleton
public class CloudWatchMeterRegistryProvider {
    static final String NAMESPACE = "quarkus.micrometer.export.cloudwatch.namespace";

    @Produces
    @Singleton
    @DefaultBean
    public CloudWatchConfig configure(CloudWatchRuntimeConfig config) {
        if (config.namespace().isEmpty()) {
            throw new ConfigurationException(NAMESPACE + " is required when the CloudWatch MeterRegistry is enabled");
        }

        CloudWatchConfig cloudWatchConfig = new CloudWatchConfigAdapter(config);
        // Checks the remaining Micrometer constraints, like the maximum batch size
        cloudWatchConfig.validate().orThrow();
        return cloudWatchConfig;
    }

    /**
     * Uses the configured region and endpoint, falling back to the AWS SDK default providers.
     * Credentials always come from the AWS SDK default credentials provider chain.
     * Applications can provide their own {@link CloudWatchAsyncClient} bean to customize it.
     */
    @Produces
    @Singleton
    @DefaultBean
    public CloudWatchAsyncClient cloudWatchAsyncClient(CloudWatchRuntimeConfig config) {
        CloudWatchAsyncClientBuilder builder = CloudWatchAsyncClient.builder();
        config.region().map(Region::of).ifPresent(builder::region);
        config.endpointOverride().ifPresent(builder::endpointOverride);
        return builder.build();
    }

    void closeCloudWatchAsyncClient(@Disposes CloudWatchAsyncClient client) {
        client.close();
    }

    @Produces
    @DefaultBean
    public CloudWatchNamingConvention namingConvention() {
        return new CloudWatchNamingConvention();
    }
}
