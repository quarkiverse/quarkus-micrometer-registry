package io.quarkiverse.micrometer.registry.datadog;

import java.util.Map;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import io.micrometer.datadog.DatadogConfig;
import io.micrometer.datadog.DatadogNamingConvention;
import io.quarkiverse.micrometer.registry.datadog.DatadogConfig.DatadogRuntimeConfig;
import io.quarkus.arc.DefaultBean;
import io.quarkus.micrometer.runtime.export.ConfigAdapter;

/**
 * @see ConditionalRegistryProducer
 */
@Singleton
public class DatadogMeterRegistryProvider {
    static final String DEFAULT_REGISTRY = "quarkus.micrometer.export.datadog.default-registry";
    static final String PREFIX = "datadog.";

    static final String PUBLISH = "datadog.publish";
    static final String ENABLED = "datadog.enabled";

    @Produces
    @Singleton
    @DefaultBean
    public DatadogConfig configure(DatadogRuntimeConfig config) {
        final Map<String, String> properties = ConfigAdapter.captureProperties(config.datadog, PREFIX);

        // Special check: if publish is set, override the value of enabled
        // Specifically, The datadog registry must be enabled for this
        // Provider to even be present. If this instance (at runtime) wants
        // to prevent metrics from being published, then it would set
        // quarkus.micrometer.export.datadog.publish=false
        if (properties.containsKey(PUBLISH)) {
            properties.put(ENABLED, properties.get(PUBLISH));
        }

        return ConfigAdapter.validate(properties::get);
    }

    @Produces
    @DefaultBean
    public DatadogNamingConvention namingConvention() {
        return new DatadogNamingConvention();
    }
}
