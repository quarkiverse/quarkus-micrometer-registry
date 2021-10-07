package io.quarkiverse.micrometer.registry.graphite;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.interceptor.Interceptor;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.graphite.GraphiteConfig;
import io.micrometer.graphite.GraphiteMeterRegistry;
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
    public GraphiteMeterRegistry registry(GraphiteConfig config,
            @GraphiteNameMapper HierarchicalNameMapper nameMapper,
            Clock clock) {
        return new GraphiteMeterRegistry(config, clock, nameMapper);
    }
}
