package io.quarkiverse.micrometer.registry.statsd;

import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import io.micrometer.core.instrument.Clock;
import io.micrometer.statsd.StatsdConfig;
import io.micrometer.statsd.StatsdMeterRegistry;
import io.quarkiverse.micrometer.registry.statsd.StatsdConfig.StatsdRuntimeConfig;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.properties.UnlessBuildProperty;
import io.quarkus.micrometer.runtime.export.ConfigAdapter;

@Singleton
public class StatsdMeterRegistryProvider {
    static final String DEFAULT_REGISTRY = "quarkus.micrometer.export.statsd.default-registry";
    static final String PREFIX = "statsd.";

    static final String PUBLISH = "statsd.publish";
    static final String ENABLED = "statsd.enabled";

    @Produces
    @Singleton
    @DefaultBean
    public StatsdConfig configure(StatsdRuntimeConfig config) {
        final Map<String, String> properties = ConfigAdapter.captureProperties(config.statsd, PREFIX);

        // Special check: if publish is set, override the value of enabled
        // Specifically, the StatsD registry must be enabled for this
        // Provider to even be present. If this instance (at runtime) wants
        // to prevent metrics from being published, then it would set
        // quarkus.micrometer.export.statsd.publish=false
        if (properties.containsKey(PUBLISH)) {
            properties.put(ENABLED, properties.get(PUBLISH));
        }

        return ConfigAdapter.validate(properties::get);
    }

    @Produces
    @Singleton
    @UnlessBuildProperty(name = DEFAULT_REGISTRY, stringValue = "false", enableIfMissing = true)
    public StatsdMeterRegistry registry(StatsdConfig config, Clock clock) {
        return new StatsdMeterRegistry(config, clock);
    }
}
