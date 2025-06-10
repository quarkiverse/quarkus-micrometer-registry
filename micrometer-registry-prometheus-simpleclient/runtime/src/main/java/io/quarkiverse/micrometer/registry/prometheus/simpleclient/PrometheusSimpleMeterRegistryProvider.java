package io.quarkiverse.micrometer.registry.prometheus.simpleclient;

import java.util.Map;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import jakarta.interceptor.Interceptor;

import io.micrometer.prometheus.PrometheusConfig;
import io.quarkiverse.micrometer.registry.prometheus.simpleclient.PrometheusSimpleConfig.PrometheusSimpleClientRuntimeConfig;
import io.quarkus.micrometer.runtime.export.ConfigAdapter;

/**
 * @see ConditionalRegistryProducer
 */
@Singleton
public class PrometheusSimpleMeterRegistryProvider {
    static final String DEFAULT_REGISTRY = "quarkus.micrometer.export.prometheus.simpleclient.default-registry";
    static final String PREFIX = "prometheus.";

    static final String PUBLISH = "prometheus.publish";
    static final String ENABLED = "prometheus.enabled";

    @Produces
    @Singleton
    @Alternative
    @Priority(Interceptor.Priority.APPLICATION + 100)
    public PrometheusConfig configure(PrometheusSimpleClientRuntimeConfig config) {
        final Map<String, String> properties = ConfigAdapter.captureProperties(config.prometheusSimpleClient(), PREFIX);

        // Special check: if publish is set, override the value of enabled
        // Specifically, The prometheus registry must be enabled for this
        // Provider to even be present. If this instance (at runtime) wants
        // to prevent metrics from being published, then it would set
        // quarkus.micrometer.export.prometheus.simpleclient.publish=false
        if (properties.containsKey(PUBLISH)) {
            properties.put(ENABLED, properties.get(PUBLISH));
        }

        return ConfigAdapter.validate(properties::get);
    }
}
