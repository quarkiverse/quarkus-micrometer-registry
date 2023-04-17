package io.quarkiverse.micrometer.registry.newrelic;

import static io.micrometer.core.instrument.config.MeterRegistryConfigValidator.check;
import static io.micrometer.core.instrument.config.MeterRegistryConfigValidator.checkAll;
import static io.micrometer.core.instrument.config.MeterRegistryConfigValidator.checkRequired;

import java.util.Map;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import io.micrometer.core.instrument.config.validate.InvalidReason;
import io.micrometer.core.instrument.config.validate.Validated;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import io.micrometer.core.instrument.util.StringUtils;
import io.micrometer.newrelic.ClientProviderType;
import io.micrometer.newrelic.NewRelicConfig;
import io.micrometer.newrelic.NewRelicNamingConvention;
import io.quarkiverse.micrometer.registry.newrelic.NewRelicConfig.NewRelicRuntimeConfig;
import io.quarkus.arc.DefaultBean;
import io.quarkus.micrometer.runtime.export.ConfigAdapter;

/**
 * @see ConditionalRegistryProducer
 */
@Singleton
public class NewRelicMeterRegistryProvider {
    static final String DEFAULT_REGISTRY = "quarkus.micrometer.export.newrelic.default-registry";
    static final String PREFIX = "newrelic.";

    static final String PUBLISH = "newrelic.publish";
    static final String ENABLED = "newrelic.enabled";

    /**
     * Configures and validates MM side properties.
     *
     * @param config The Runtime configuration object
     * @return The output MM library configuration
     */
    @Produces
    @Singleton
    @DefaultBean
    public NewRelicConfig configure(NewRelicRuntimeConfig config) {
        final Map<String, String> properties = ConfigAdapter.captureProperties(config.newrelic, PREFIX);

        // Special check: if publish is set, override the value of enabled
        // Specifically, The new relic registry must be enabled for this
        // Provider to even be present. If this instance (at runtime) wants
        // to prevent metrics from being published, then it would set
        // quarkus.micrometer.export.newrelic.publish=false
        if (properties.containsKey(PUBLISH)) {
            properties.put(ENABLED, properties.get(PUBLISH));
        }

        return ConfigAdapter.validate(new NewRelicConfig() {
            @Override
            public String get(String key) {
                return properties.get(key);
            }

            @Override
            public Validated<?> validate() {
                if (clientProviderType() == ClientProviderType.INSIGHTS_AGENT) {
                    return validateForInsightsAgent();
                } else {
                    return validateForInsightsApi();
                }
            }

            public Validated<?> validateForInsightsAgent() {
                return checkAll(this,
                        c -> StepRegistryConfig.validate(c),
                        check("eventType", NewRelicConfig::eventType)
                                .andThen(
                                        v -> v.invalidateWhen(type -> StringUtils.isBlank(type) && !meterNameEventTypeEnabled(),
                                                "event type is required when not using the meter name as the event type",
                                                InvalidReason.MISSING)),
                        checkRequired("clientProviderType", NewRelicConfig::clientProviderType));
            }

            public Validated<?> validateForInsightsApi() {
                return checkAll(this,
                        c -> validateForInsightsAgent(),
                        check("uri", NewRelicConfig::uri)
                                .andThen(v -> v.invalidateWhen(StringUtils::isBlank,
                                        "is required when publishing to Insights API",
                                        InvalidReason.MISSING)),
                        check("apiKey", NewRelicConfig::apiKey)
                                .andThen(v -> v.invalidateWhen(StringUtils::isBlank,
                                        "is required when publishing to Insights API",
                                        InvalidReason.MISSING)),
                        check("accountId", NewRelicConfig::accountId)
                                .andThen(v -> v.invalidateWhen(StringUtils::isBlank,
                                        "is required when publishing to Insights API",
                                        InvalidReason.MISSING)));
            }
        });
    }

    @Produces
    @DefaultBean
    public NewRelicNamingConvention namingConvention() {
        return new NewRelicNamingConvention();
    }
}
