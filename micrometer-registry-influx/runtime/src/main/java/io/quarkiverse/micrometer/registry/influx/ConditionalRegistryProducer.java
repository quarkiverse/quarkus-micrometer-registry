package io.quarkiverse.micrometer.registry.influx;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.interceptor.Interceptor;

import io.micrometer.core.instrument.Clock;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
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
    public InfluxMeterRegistry registry(InfluxConfig config, Clock clock) {
        return new InfluxMeterRegistry(config, clock);
    }
}
