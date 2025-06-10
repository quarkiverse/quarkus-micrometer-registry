package io.quarkiverse.micrometer.registry.prometheus.simpleclient.exemplars;

import java.util.Optional;
import java.util.function.Function;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.interceptor.Interceptor;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.prometheus.client.exemplars.DefaultExemplarSampler;
import io.prometheus.client.exemplars.ExemplarSampler;
import io.prometheus.client.exemplars.tracer.common.SpanContextSupplier;
import io.quarkus.opentelemetry.runtime.QuarkusContextStorage;

public class OpentelemetryExemplarSamplerProvider {

    @Produces
    @Alternative
    @Priority(Interceptor.Priority.APPLICATION + 100)
    public Optional<ExemplarSampler> exemplarSampler() {
        return Optional.of(new DefaultExemplarSampler(new SpanContextSupplier() {
            @Override
            public String getTraceId() {
                return get(SpanContext::getTraceId);
            }

            @Override
            public String getSpanId() {
                return get(SpanContext::getSpanId);
            }

            @Override
            public boolean isSampled() {
                return Boolean.TRUE.equals(get(SpanContext::isSampled));
            }

            private <T> T get(Function<SpanContext, T> valueExtractor) {
                return Optional.ofNullable(Span.fromContextOrNull(QuarkusContextStorage.INSTANCE.current()))
                        .map(Span::getSpanContext)
                        .map(valueExtractor)
                        .orElse(null);
            }
        }));
    }
}
