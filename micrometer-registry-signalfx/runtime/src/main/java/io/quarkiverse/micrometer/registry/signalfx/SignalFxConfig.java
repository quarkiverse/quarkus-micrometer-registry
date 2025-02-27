package io.quarkiverse.micrometer.registry.signalfx;

import java.util.Map;
import java.util.Optional;

import io.quarkus.micrometer.runtime.config.MicrometerConfig;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithParentName;

public interface SignalFxConfig {

    @ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
    @ConfigMapping(prefix = "quarkus.micrometer.export.signalfx")
    public interface SignalFxBuildConfig extends MicrometerConfig.CapabilityEnabled {
        /**
         * Support for export to SignalFx.
         * <p>
         * Support for SignalFx will be enabled if Micrometer
         * support is enabled, the SignalFx registry extension is enabled
         * and either this value is true, or this value is unset and
         * {@code quarkus.micrometer.registry-enabled-default} is true.
         */
        @Override
        Optional<Boolean> enabled();

        /**
         * By default, this extension will create a SignalFx MeterRegistry instance.
         * <p>
         * Use this attribute to veto the creation of the default SignalFx MeterRegistry.
         */
        @WithDefault("true")
        boolean defaultRegistry();
    }

    /**
     * Runtime configuration for SignalFX MeterRegistry
     */
    @ConfigRoot(phase = ConfigPhase.RUN_TIME)
    @ConfigMapping(prefix = "quarkus.micrometer.export.signalfx")
    public interface SignalFxRuntimeConfig {
        // @formatter:off
        /**
         * SignalFx registry configuration properties.
         *
         * A property source for configuration of the SignalFx MeterRegistry,
         * see https://micrometer.io/docs/registry/signalFx.
         *
         * Available values:
         *
         * [cols=2]
         * !===
         * h!Property=Default
         * h!Description
         *
         * !`access-token=MY_ACCESS_TOKEN`
         * !Define the access token required to push data to SignalFx
         *
         * !`source=identifier`
         * !Unique identifier for the app instance that is publishing metrics to SignalFx.
         * Defaults to the local host name.
         *
         * !`uri=https://ingest.signalfx.com`
         * !Define the the URI to ship metrics to. Use this attribute to specify
         * the location of an internal proxy, if necessary.
         *
         * !`publish=true`
         * !By default, gathered metrics will be published to SignalFx when the MeterRegistry is enabled.
         * Use this attribute to selectively disable publication of metrics in some environments.
         *
         * !`step=1m`
         * !The interval at which metrics are sent to SignalFx Monitoring. The default is 1 minute.
         * !===
         *
         * Other Micrometer configuration attributes can also be specified.
         *
         * @asciidoclet
         */
        // @formatter:on
        @WithParentName
        Map<String, String> signalfx();
    }
}
