package io.quarkiverse.micrometer.registry.signalfx;

import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import io.micrometer.signalfx.SignalFxConfig;
import io.micrometer.signalfx.SignalFxNamingConvention;
import io.quarkiverse.micrometer.registry.signalfx.SignalFxConfig.SignalFxRuntimeConfig;
import io.quarkus.arc.DefaultBean;
import io.quarkus.micrometer.runtime.export.ConfigAdapter;

/**
 * @see ConditionalRegistryProducer
 */
@Singleton
public class SignalFxMeterRegistryProvider {
    static final String DEFAULT_REGISTRY = "quarkus.micrometer.export.signalfx.default-registry";
    static final String PREFIX = "signalfx.";

    static final String PUBLISH = "signalfx.publish";
    static final String ENABLED = "signalfx.enabled";

    @Produces
    @Singleton
    @DefaultBean
    public SignalFxConfig configure(SignalFxRuntimeConfig config) {
        final Map<String, String> properties = ConfigAdapter.captureProperties(config.signalfx, PREFIX);

        // Special check: if publish is set, override the value of enabled
        // Specifically, The signalfx registry must be enabled for this
        // Provider to even be present. If this instance (at runtime) wants
        // to prevent metrics from being published, then it would set
        // quarkus.micrometer.export.signalfx.publish=false
        if (properties.containsKey(PUBLISH)) {
            properties.put(ENABLED, properties.get(PUBLISH));
        }

        return ConfigAdapter.validate(properties::get);
    }

    @Produces
    @DefaultBean
    public SignalFxNamingConvention namingConvention() {
        return new SignalFxNamingConvention();
    }
}
