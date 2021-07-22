package io.quarkiverse.micrometer.registry.jmx;

import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import io.quarkiverse.micrometer.registry.jmx.JmxConfig.JmxRuntimeConfig;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.properties.UnlessBuildProperty;
import io.quarkus.micrometer.runtime.export.ConfigAdapter;

@Singleton
public class JmxMeterRegistryProvider {
    static final String DEFAULT_REGISTRY = "quarkus.micrometer.export.jmx.default-registry";
    static final String PREFIX = "jmx.";

    static final String PUBLISH = "jmx.publish";
    static final String ENABLED = "jmx.enabled";

    @Produces
    @Singleton
    @DefaultBean
    public HierarchicalNameMapper config() {
        return HierarchicalNameMapper.DEFAULT;
    }

    @Produces
    @Singleton
    @DefaultBean
    public JmxConfig configure(JmxRuntimeConfig config) {
        final Map<String, String> properties = ConfigAdapter.captureProperties(config.jmx, PREFIX);

        // Special check: if publish is set, override the value of enabled
        // Specifically, The datadog registry must be enabled for this
        // Provider to even be present. If this instance (at runtime) wants
        // to prevent metrics from being published, then it would set
        // quarkus.micrometer.export.jmx.publish=false
        if (properties.containsKey(PUBLISH)) {
            properties.put(ENABLED, properties.get(PUBLISH));
        }

        return ConfigAdapter.validate(properties::get);
    }

    @Produces
    @Singleton
    @UnlessBuildProperty(name = DEFAULT_REGISTRY, stringValue = "false", enableIfMissing = true)
    public JmxMeterRegistry registry(JmxConfig config, Clock clock, HierarchicalNameMapper nameMapper) {
        return new JmxMeterRegistry(config, clock, nameMapper);
    }
}
