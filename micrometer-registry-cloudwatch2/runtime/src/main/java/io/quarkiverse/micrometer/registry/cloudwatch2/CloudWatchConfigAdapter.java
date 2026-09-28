package io.quarkiverse.micrometer.registry.cloudwatch2;

import java.time.Duration;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.quarkiverse.micrometer.registry.cloudwatch2.CloudWatchConfig.CloudWatchRuntimeConfig;

/**
 * Exposes the Quarkus CloudWatch runtime configuration as a Micrometer {@link CloudWatchConfig}.
 */
class CloudWatchConfigAdapter implements CloudWatchConfig {

    private final CloudWatchRuntimeConfig config;

    CloudWatchConfigAdapter(CloudWatchRuntimeConfig config) {
        this.config = config;
    }

    @Override
    public String get(String key) {
        // All supported attributes are mapped explicitly below
        return null;
    }

    @Override
    public String namespace() {
        return config.namespace().orElse(null);
    }

    @Override
    public boolean enabled() {
        // The registry must be enabled for this config to exist: publish only controls the export
        return config.publish();
    }

    @Override
    public Duration step() {
        return config.step();
    }

    @Override
    public boolean highResolution() {
        return config.highResolution();
    }

    @Override
    public int batchSize() {
        return config.batchSize();
    }
}
