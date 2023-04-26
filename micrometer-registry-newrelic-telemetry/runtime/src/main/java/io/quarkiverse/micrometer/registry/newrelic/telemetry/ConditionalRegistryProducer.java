package io.quarkiverse.micrometer.registry.newrelic.telemetry;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import jakarta.interceptor.Interceptor;

import com.newrelic.telemetry.micrometer.NewRelicRegistry;
import com.newrelic.telemetry.micrometer.NewRelicRegistryConfig;

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
    public NewRelicRegistry registry(NewRelicRegistryConfig config) {
        return NewRelicRegistry.builder(config)
                .build();
    }
}
