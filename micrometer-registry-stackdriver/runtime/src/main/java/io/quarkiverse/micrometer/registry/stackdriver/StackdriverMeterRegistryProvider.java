package io.quarkiverse.micrometer.registry.stackdriver;

import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import io.micrometer.stackdriver.StackdriverConfig;
import io.micrometer.stackdriver.StackdriverNamingConvention;
import io.quarkiverse.micrometer.registry.stackdriver.StackdriverConfig.StackdriverRuntimeConfig;
import io.quarkus.arc.DefaultBean;
import io.quarkus.micrometer.runtime.export.ConfigAdapter;

/**
 * @see ConditionalRegistryProducer
 */
@Singleton
public class StackdriverMeterRegistryProvider {
    static final String DEFAULT_REGISTRY = "quarkus.micrometer.export.stackdriver.default-registry";
    static final String PREFIX = "stackdriver.";

    static final String PUBLISH = "stackdriver.publish";
    static final String ENABLED = "stackdriver.enabled";

    @Produces
    @Singleton
    @DefaultBean
    public StackdriverConfig configure(StackdriverRuntimeConfig config) throws Throwable {
        final Map<String, String> properties = ConfigAdapter.captureProperties(config.stackdriver, PREFIX);

        // Special check: if publish is set, override the value of enabled
        // Specifically, the StackDriver registry must be enabled for this
        // Provider to even be present. If this instance (at runtime) wants
        // to prevent metrics from being published, then it would set
        // quarkus.micrometer.export.stackdriver.publish=false
        if (properties.containsKey(PUBLISH)) {
            properties.put(ENABLED, properties.get(PUBLISH));
        }

        return ConfigAdapter.validate(properties::get);
    }

    @Produces
    @DefaultBean
    public StackdriverNamingConvention namingConvention() {
        return new StackdriverNamingConvention();
    }
}
