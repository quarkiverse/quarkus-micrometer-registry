package io.quarkiverse.micrometer.registry.prometheus.simpleclient;

import java.util.Map;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusDurationNamingConvention;
import io.micrometer.prometheus.PrometheusNamingConvention;
import io.prometheus.client.CollectorRegistry;
import io.quarkiverse.micrometer.registry.prometheus.simpleclient.PrometheusSimpleConfig.PrometheusSimpleClientRuntimeConfig;
import io.quarkus.arc.DefaultBean;
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
    @DefaultBean
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

    @Produces
    @DefaultBean
    public PrometheusNamingConvention namingConvention() {
        return new PrometheusNamingConvention();
    }

    @Produces
    @DefaultBean
    public PrometheusDurationNamingConvention durationNamingConvention() {
        return new PrometheusDurationNamingConvention();
    }

    @Produces
    @DefaultBean
    public CollectorRegistry collectorRegistry() {
        return new CollectorRegistry(true);
    }
}
