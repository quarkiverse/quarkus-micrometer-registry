package io.quarkiverse.micrometer.registry.prometheus.simpleclient;

import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import jakarta.interceptor.Interceptor;

import io.micrometer.core.instrument.Clock;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exemplars.ExemplarSampler;

//@Singleton
public class ConditionalRegistryProducer {

    /**
     * This producer is added as a bean by the Processor IFF the default registry
     * instance has been enabled.
     */
    @Produces
    @Singleton
    @Alternative
    @Priority(Interceptor.Priority.APPLICATION + 100)
    public PrometheusMeterRegistry registry(PrometheusConfig config,
            CollectorRegistry collectorRegistry,
            Optional<ExemplarSampler> exemplarSampler,
            Clock clock) {
        return new PrometheusMeterRegistry(config, collectorRegistry, clock, exemplarSampler.orElse(null));
    }
}
