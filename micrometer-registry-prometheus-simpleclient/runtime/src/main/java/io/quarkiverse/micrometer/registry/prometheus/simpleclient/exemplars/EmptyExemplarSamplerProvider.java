package io.quarkiverse.micrometer.registry.prometheus.simpleclient.exemplars;

import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.interceptor.Interceptor;

import io.prometheus.client.exemplars.ExemplarSampler;

public class EmptyExemplarSamplerProvider {

    @Produces
    @Alternative
    @Priority(Interceptor.Priority.APPLICATION + 100)
    public Optional<ExemplarSampler> exemplarSampler() {
        return Optional.empty();
    }
}
