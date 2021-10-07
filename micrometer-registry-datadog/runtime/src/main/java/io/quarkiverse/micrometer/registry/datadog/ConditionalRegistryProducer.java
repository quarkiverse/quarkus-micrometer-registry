package io.quarkiverse.micrometer.registry.datadog;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.interceptor.Interceptor;

import io.micrometer.core.instrument.Clock;
import io.micrometer.datadog.DatadogConfig;
import io.micrometer.datadog.DatadogMeterRegistry;
import io.quarkus.arc.AlternativePriority;

@Singleton
public class ConditionalRegistryProducer {
    /**
     * This producer is added as a bean by the Processor IFF the default registry
     * instance has been enabled.
     */
    @Produces
    @Singleton
    @AlternativePriority(Interceptor.Priority.APPLICATION + 100)
    public DatadogMeterRegistry registry(DatadogConfig config, Clock clock) {
        return DatadogMeterRegistry.builder(config)
                .clock(clock)
                .build();
    }
}
