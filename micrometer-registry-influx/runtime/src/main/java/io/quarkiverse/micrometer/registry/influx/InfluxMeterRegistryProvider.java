package io.quarkiverse.micrometer.registry.influx;

import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import io.micrometer.core.instrument.Clock;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
import io.quarkiverse.micrometer.registry.influx.InfluxConfig.InfluxRuntimeConfig;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.properties.UnlessBuildProperty;
import io.quarkus.micrometer.runtime.export.ConfigAdapter;

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
        final Map<String, String> properties = ConfigAdapter.captureProperties(config.influxdb, PREFIX);

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

    @Produces
    @Singleton
    @UnlessBuildProperty(name = DEFAULT_REGISTRY, stringValue = "false", enableIfMissing = true)
    public InfluxMeterRegistry registry(InfluxConfig config, Clock clock) {
        return new InfluxMeterRegistry(config, clock);
    }
}
