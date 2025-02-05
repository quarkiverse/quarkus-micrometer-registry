package io.quarkiverse.micrometer.registry.newrelic.telemetry;

import java.util.Map;
import java.util.Optional;

import io.quarkus.micrometer.runtime.config.MicrometerConfig;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithParentName;

public interface NewRelicConfig {

    @ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
    @ConfigMapping(prefix = "quarkus.micrometer.export.newrelic.telemetry")
    public interface NewRelicBuildConfig extends MicrometerConfig.CapabilityEnabled {
        /**
         * Support for export to New Relic
         * <p>
         * Support for New Relic will be enabled if Micrometer
         * support is enabled, the New Relic registry extension is enabled
         * and either this value is true, or this value is unset and
         * {@code quarkus.micrometer.registry-enabled-default} is true.
         */
        @Override
        Optional<Boolean> enabled();

        /**
         * By default, this extension will create a New Relic MeterRegistry instance.
         * <p>
         * Use this attribute to veto the creation of the default New Relic MeterRegistry.
         */
        @WithDefault("true")
        boolean defaultRegistry();
    }

    /**
     * Runtime configuration for New Relic MeterRegistry
     */
    @ConfigRoot(phase = ConfigPhase.RUN_TIME)
    @ConfigMapping(prefix = "quarkus.micrometer.export.newrelic.telemetry")
    public interface NewRelicRuntimeConfig {
        // @formatter:off
        /**
         * New Relic MeterRegistry configuration properties.
         *
         * A property source for configuration of the New Relic MeterRegistry to push
         * metrics using the New Relic library, see https://github.com/newrelic/micrometer-registry-newrelic.
         *
         * Available values:
         *
         * [cols=2]
         * !===
         * h!Property=Default
         * h!Description
         *
         * !`apiKey=YOUR_KEY`
         * !Define the key used to push data using the New Relic API
         *
         * !`uri=DESTINATION_URI`
         * ! The URI for the New Relic metric API. Only necessary if you need to override the default URI.
         *
         * !`publish=true`
         * !By default, gathered metrics will be published to New Relic when the MeterRegistry is enabled.
         * Use this attribute to selectively disable publication of metrics in some environments.
         *
         * Other Micrometer configuration attributes can also be specified. See the
         * https://github.com/newrelic/micrometer-registry-newrelic/blob/main/src/main/java/com/newrelic/telemetry/micrometer/NewRelicRegistryConfig.java[configuration class].
         * !===
         *
         * Other Micrometer configuration attributes can also be specified.
         *
         * @asciidoclet
         */
        // @formatter:on
        @WithParentName
        Map<String, String> newrelic();
    }
}
