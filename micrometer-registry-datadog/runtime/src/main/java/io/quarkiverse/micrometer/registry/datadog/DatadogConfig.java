package io.quarkiverse.micrometer.registry.datadog;

import java.util.Map;
import java.util.Optional;

import io.quarkus.micrometer.runtime.config.MicrometerConfig;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithParentName;

public interface DatadogConfig {

    @ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
    @ConfigMapping(prefix = "quarkus.micrometer.export.datadog")
    public interface DatadogBuildConfig extends MicrometerConfig.CapabilityEnabled {
        /**
         * Support for export to Datadog
         * <p>
         * Support for Datadog will be enabled if Micrometer
         * support is enabled, the Datadog registry extension is enabled
         * and either this value is true, or this value is unset and
         * {@code quarkus.micrometer.registry-enabled-default} is true.
         */
        @Override
        Optional<Boolean> enabled();

        /**
         * By default, this extension will create a Datadog MeterRegistry instance.
         * <p>
         * Use this attribute to veto the creation of the default Datadog MeterRegistry.
         */
        @WithDefault("true")
        boolean defaultRegistry();
    }

    /**
     * Runtime configuration for Datadog MeterRegistry
     */
    @ConfigRoot(phase = ConfigPhase.RUN_TIME)
    @ConfigMapping(prefix = "quarkus.micrometer.export.datadog")
    public interface DatadogRuntimeConfig {
        // @formatter:off
        /**
         * Datadog MeterRegistry configuration properties.
         *
         * A property source for configuration of the Datadog MeterRegistry to push
         * metrics using the Datadog API, see https://micrometer.io/docs/registry/datadog.
         *
         * Available values:
         *
         * [cols=2]
         * !===
         * h!Property=Default
         * h!Description
         *
         * !`apiKey=YOUR_KEY`
         * !Define the key used to push data using the Datadog API
         *
         * !`publish=true`
         * !By default, gathered metrics will be published to Datadog when the MeterRegistry is enabled.
         * Use this attribute to selectively disable publication of metrics in some environments.
         *
         * !`step=1m`
         * !The interval at which metrics are sent to Datadog. The default is 1 minute.
         * !===
         *
         * Other Micrometer configuration attributes can also be specified.
         *
         * @asciidoclet
         */
        // @formatter:on
        @WithParentName
        Map<String, String> datadog();
    }
}
