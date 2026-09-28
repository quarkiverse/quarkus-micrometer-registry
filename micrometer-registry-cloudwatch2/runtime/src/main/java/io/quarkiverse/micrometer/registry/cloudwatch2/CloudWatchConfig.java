package io.quarkiverse.micrometer.registry.cloudwatch2;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import io.quarkus.micrometer.runtime.config.MicrometerConfig;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

public interface CloudWatchConfig {

    @ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
    @ConfigMapping(prefix = "quarkus.micrometer.export.cloudwatch")
    public interface CloudWatchBuildConfig extends MicrometerConfig.CapabilityEnabled {
        /**
         * Support for export to AWS CloudWatch
         * <p>
         * Support for CloudWatch will be enabled if Micrometer
         * support is enabled, the CloudWatch registry extension is enabled
         * and either this value is true, or this value is unset and
         * {@code quarkus.micrometer.registry-enabled-default} is true.
         */
        @Override
        Optional<Boolean> enabled();

        /**
         * By default, this extension will create a CloudWatch MeterRegistry instance.
         * <p>
         * Use this attribute to veto the creation of the default CloudWatch MeterRegistry.
         */
        @WithDefault("true")
        boolean defaultRegistry();
    }

    /**
     * Runtime configuration for CloudWatch MeterRegistry
     */
    @ConfigRoot(phase = ConfigPhase.RUN_TIME)
    @ConfigMapping(prefix = "quarkus.micrometer.export.cloudwatch")
    public interface CloudWatchRuntimeConfig {
        /**
         * The CloudWatch namespace metrics are published to.
         * <p>
         * Required when the CloudWatch MeterRegistry is enabled.
         */
        Optional<String> namespace();

        /**
         * By default, gathered metrics will be published to CloudWatch when the MeterRegistry is enabled.
         * <p>
         * Use this attribute to selectively disable publication of metrics in some environments.
         */
        @WithDefault("true")
        boolean publish();

        /**
         * The interval at which metrics are sent to CloudWatch.
         */
        @WithDefault("1m")
        Duration step();

        /**
         * Publish metrics with a storage resolution of 1 second instead of 1 minute.
         */
        @WithDefault("false")
        boolean highResolution();

        /**
         * The number of metrics sent per request to CloudWatch (at most 1000).
         */
        @WithDefault("20")
        int batchSize();

        /**
         * The AWS region of the default {@code CloudWatchAsyncClient}.
         * <p>
         * If not set, the AWS SDK default region provider chain is used.
         */
        Optional<String> region();

        /**
         * Overrides the endpoint of the default {@code CloudWatchAsyncClient}, for example to use a local AWS emulator.
         * <p>
         * If not set, the AWS SDK default endpoint for the region is used.
         */
        Optional<URI> endpointOverride();
    }
}
