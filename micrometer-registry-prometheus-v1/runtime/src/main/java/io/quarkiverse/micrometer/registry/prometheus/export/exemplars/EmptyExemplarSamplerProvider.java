package io.quarkiverse.micrometer.registry.prometheus.export.exemplars;

import java.util.Optional;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;

import io.prometheus.metrics.tracer.common.SpanContext;
import io.quarkus.arc.DefaultBean;
import io.quarkus.micrometer.runtime.export.exemplars.OpenTelemetryContextUnwrapper;

public class EmptyExemplarSamplerProvider {

    @Produces
    public Optional<SpanContext> exemplarSampler() {
        return Optional.empty();
    }

    @Produces
    @RequestScoped
    @DefaultBean
    public OpenTelemetryContextUnwrapper noopOpenTelemetryExemplarContextUnwrapper() {
        return new NoopOpenTelemetryExemplarContextUnwrapper();
    }
}
