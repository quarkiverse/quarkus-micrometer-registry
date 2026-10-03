package io.quarkiverse.micrometer.registry.cloudwatch2;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import jakarta.interceptor.Interceptor;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

@Singleton
public class ConditionalRegistryProducer {
    /**
     * This producer is added as a bean by the Processor IFF the default registry
     * instance has been enabled.
     */
    @Produces
    @Singleton
    @Alternative
    @Priority(Interceptor.Priority.APPLICATION + 100)
    public CloudWatchMeterRegistry registry(CloudWatchConfig config, Clock clock, CloudWatchAsyncClient client) {
        return new CloudWatchMeterRegistry(config, clock, client);
    }
}
