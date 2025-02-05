package io.quarkiverse.micrometer.registry.influx;

import java.util.Map;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import io.micrometer.influx.InfluxConfig;
import io.quarkiverse.micrometer.registry.influx.InfluxConfig.InfluxRuntimeConfig;
import io.quarkus.arc.DefaultBean;
import io.quarkus.micrometer.runtime.export.ConfigAdapter;

/**
 * @see ConditionalRegistryProducer
 */
@Singleton
public class InfluxMeterRegistryProvider {

    static final String DEFAULT_REGISTRY = "quarkus.micrometer.export.influx.default-registry";
    static final String PREFIX = "influx.";

    static final String PUBLISH = "influx.publish";
    static final String ENABLED = "influx.enabled";

    @Produces
    @Singleton
    @DefaultBean
    public InfluxConfig configure(InfluxRuntimeConfig config) {
        final Map<String, String> properties = ConfigAdapter.captureProperties(config.influxdb(), PREFIX);

        // Special check: if publish is set, override the value of enabled
        // Specifically, the InfluxDB registry must be enabled for this
        // Provider to even be present. If this instance (at runtime) wants
        // to prevent metrics from being published, then it would set
        // quarkus.micrometer.export.influxdb.publish=false
        if (properties.containsKey(PUBLISH)) {
            properties.put(ENABLED, properties.get(PUBLISH));
        }

        return ConfigAdapter.validate(properties::get);
    }
}
