package io.quarkiverse.micrometer.registry.graphite;

import java.util.Map;
import java.util.Optional;

import io.quarkus.micrometer.runtime.config.MicrometerConfig;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithParentName;

public interface GraphiteConfig {

    @ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
    @ConfigMapping(prefix = "quarkus.micrometer.export.graphite")
    public interface GraphiteBuildConfig extends MicrometerConfig.CapabilityEnabled {
        /**
         * Support for export to Graphite.
         * <p>
         * Support for Graphite will be enabled if Micrometer
         * support is enabled, the Graphite registry extension is enabled
         * and either this value is true, or this value is unset and
         * {@code quarkus.micrometer.registry-enabled-default} is true.
         */
        @Override
        Optional<Boolean> enabled();

        /**
         * By default, this extension will create a Graphite MeterRegistry instance.
         * <p>
         * Use this attribute to veto the creation of the default Graphite MeterRegistry.
         */
        @WithDefault("true")
        boolean defaultRegistry();
    }

    /**
     * Runtime configuration for Graphite MeterRegistry
     */
    @ConfigRoot(phase = ConfigPhase.RUN_TIME)
    @ConfigMapping(prefix = "quarkus.micrometer.export.graphite")
    public interface GraphiteRuntimeConfig {
        // @formatter:off
        /**
         * Graphite registry configuration properties.
         *
         * A property source for configuration of the Graphite MeterRegistry,
         * see https://micrometer.io/docs/registry/graphite.
         *
         * Available values:
         *
         * [cols=2]
         * !===
         * h!Property=Default
         * h!Description
         *
         * !`graphiteTagsEnabled=boolean`
         * !Whether Graphite tags should be used, as opposed to a hierarchical naming convention.
         * Defaults to true if no values are present for tagsAsPrefix
         *
         * !`rateUnits=SECONDS`
         * !Base time unit used to report rates.
         * Defaults to SECONDS.
         *
         * !`durationUnits=MILLISECONDS`
         * !Base time unit used to report durations.
         * Defaults to MILLISECONDS
         *
         * !`host=localhost`
         * !Host of the Graphite server to receive exported metrics.
         * The default to localhost.
         *
         * !`port=2004`
         * !Port of the Graphite server to receive exported metrics.
         * The default to 2004.
         *
         * !`protocol=PICKLED`
         * !Protocol to use while shipping data to Graphite
         * (valid values are PLAINTEXT, UDP and PICKLED).
         * The default to PICKLED.
         *
         * !`publish=true`
         * !By default, gathered metrics will be published to Graphite when the MeterRegistry is enabled.
         * Use this attribute to selectively disable publication of metrics in some environments.
         *
         * !`step=1m`
         * !The interval at which metrics are sent to Graphite.
         * Defaults to 1 minute.
         * !===
         *
         * Other Micrometer configuration attributes can also be specified.
         *
         * @asciidoclet
         */
        // @formatter:on
        @WithParentName
        Map<String, String> graphite();
    }
}
