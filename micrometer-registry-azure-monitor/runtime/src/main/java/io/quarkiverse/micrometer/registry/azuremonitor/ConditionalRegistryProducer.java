package io.quarkiverse.micrometer.registry.azuremonitor;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.interceptor.Interceptor;

import io.micrometer.azuremonitor.AzureMonitorConfig;
import io.micrometer.azuremonitor.AzureMonitorMeterRegistry;
import io.micrometer.core.instrument.Clock;
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
    public AzureMonitorMeterRegistry registry(AzureMonitorConfig config, Clock clock) {
        return new AzureMonitorMeterRegistry(config, clock);
    }
}
