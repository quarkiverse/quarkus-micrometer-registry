package io.quarkiverse.micrometer.registry.newrelic.telemetry;

import static io.micrometer.core.instrument.config.MeterRegistryConfigValidator.check;
import static io.micrometer.core.instrument.config.MeterRegistryConfigValidator.checkAll;

import java.util.Map;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import com.newrelic.telemetry.micrometer.NewRelicRegistryConfig;

import io.micrometer.core.instrument.config.validate.InvalidReason;
import io.micrometer.core.instrument.config.validate.Validated;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import io.micrometer.core.instrument.util.StringUtils;
import io.quarkiverse.micrometer.registry.newrelic.telemetry.NewRelicConfig.NewRelicRuntimeConfig;
import io.quarkiverse.micrometer.registry.newrelic.telemetry.newrelic.NewRelicRegistryConfigAlternative;
import io.quarkus.arc.DefaultBean;
import io.quarkus.micrometer.runtime.export.ConfigAdapter;

/**
 * @see ConditionalRegistryProducer
 */
@Singleton
public class NewRelicMeterRegistryProvider {

    /**
     * These static properties define how we get the new relic side properties from the quarkus defined ones.
     */
    public static final String PREFIX = "newrelic.telemetry";

    static final String PROPERTY_MATCHER = PREFIX + ".";

    static final String PUBLISH = PREFIX + ".publish";
    static final String ENABLED = PREFIX + ".enabled";

    /**
     * Configures and validates output New Relic registry lib
     *
     * @param config runtime side config
     * @return The output New Relic library configuration
     */
    @Produces
    @Singleton
    @DefaultBean
    public NewRelicRegistryConfig configure(NewRelicRuntimeConfig config) {
        final Map<String, String> properties = ConfigAdapter.captureProperties(config.newrelic, PROPERTY_MATCHER);

        // Special check: if publish is set, override the value of enabled
        // Specifically, The new relic registry must be enabled for this
        // Provider to even be present. If this instance (at runtime) wants
        // to prevent metrics from being published, then it would set
        // quarkus.micrometer.export.newrelic.publish=false
        if (properties.containsKey(PUBLISH)) {
            properties.put(ENABLED, properties.get(PUBLISH));
        }
        return ConfigAdapter.validate(new NewRelicRegistryConfigAlternative() {
            @Override
            public String get(String key) {
                return properties.get(key);
            }

            @Override
            public Validated<?> validate() {
                return checkAll(this,
                        c -> StepRegistryConfig.validate(c),
                        check("uri", NewRelicRegistryConfigAlternative::uri)
                                .andThen(v -> v.invalidateWhen(StringUtils::isBlank,
                                        "is required when publishing to Insights API",
                                        InvalidReason.MISSING)),
                        check("newrelic.telemetry.apiKey", NewRelicRegistryConfigAlternative::apiKey)
                                .andThen(v -> v.invalidateWhen(StringUtils::isBlank,
                                        "is required when publishing to Insights API",
                                        InvalidReason.MISSING)));
            }
        });
    }
}
