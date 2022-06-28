package io.quarkiverse.micrometer.registry.newrelic.telemetry.newrelic;

import static io.quarkiverse.micrometer.registry.newrelic.telemetry.NewRelicMeterRegistryProvider.PREFIX;

import com.newrelic.telemetry.micrometer.NewRelicRegistryConfig;

import io.micrometer.core.instrument.config.validate.InvalidReason;
import io.micrometer.core.instrument.config.validate.Validated;
import io.micrometer.core.instrument.config.validate.ValidationException;

/**
 * Interface to work around the throw of a deprecated exception on the New Relic side library
 */
public interface NewRelicRegistryConfigAlternative extends NewRelicRegistryConfig {

    @Override
    default String prefix() {
        return PREFIX;
    }

    @Override
    default String apiKey() {
        final String effectiveKey = prefix() + ".apiKey";
        final String apiKey = get(effectiveKey);
        if (apiKey == null) {
            throw new ValidationException(Validated.invalid(
                    effectiveKey,
                    null,
                    "is required when publishing to Insights API",
                    InvalidReason.MISSING));
        }
        return apiKey;
    }
}
