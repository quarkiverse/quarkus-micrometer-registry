package io.quarkiverse.micrometer.registry.otlp;

import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import io.micrometer.registry.otlp.OtlpConfig;
import io.quarkiverse.micrometer.registry.otlp.OtlpConfig.OtlpRuntimeConfig;
import io.quarkus.arc.DefaultBean;
import io.quarkus.micrometer.runtime.export.ConfigAdapter;

/**
 * @see ConditionalRegistryProducer
 */
@Singleton
public class OtlpMeterRegistryProvider {
    static final String PREFIX = "otlp.";

    static final String PUBLISH = "otlp.publish";
    static final String ENABLED = "otlp.enabled";

    @Produces
    @Singleton
    @DefaultBean
    public OtlpConfig configure(OtlpRuntimeConfig config) {
        final Map<String, String> properties = ConfigAdapter.captureProperties(config.otlp, PREFIX);

        // Special check: if publish is set, override the value of enabled
        // Specifically, the StackDriver registry must be enabled for this
        // Provider to even be present. If this instance (at runtime) wants
        // to prevent metrics from being published, then it would set
        // quarkus.micrometer.export.otlp.publish=false
        if (properties.containsKey(PUBLISH)) {
            properties.put(ENABLED, properties.get(PUBLISH));
        }

        return ConfigAdapter.validate(properties::get);
    }
}
