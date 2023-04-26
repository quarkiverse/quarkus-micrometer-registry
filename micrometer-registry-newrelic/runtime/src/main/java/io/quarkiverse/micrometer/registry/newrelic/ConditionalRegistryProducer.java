package io.quarkiverse.micrometer.registry.newrelic;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import jakarta.interceptor.Interceptor;

import io.micrometer.core.instrument.Clock;
import io.micrometer.newrelic.NewRelicConfig;
import io.micrometer.newrelic.NewRelicMeterRegistry;

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
    public NewRelicMeterRegistry registry(NewRelicConfig config, Clock clock) {
        return NewRelicMeterRegistry.builder(config)
                .clock(clock)
                .build();
    }
}
