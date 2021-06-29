package io.quarkiverse.micrometer.registry.datadog;

import java.util.Map;
import java.util.Optional;

import io.quarkus.micrometer.runtime.config.MicrometerConfig;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

public class DatadogConfig {

    @ConfigRoot(name = "micrometer.export.datadog", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
    public static class DatadogBuildConfig implements MicrometerConfig.CapabilityEnabled {
        /**
         * Support for export to Datadog
         * <p>
         * Support for Datadog will be enabled if Micrometer
         * support is enabled, the Datadog registry extension is enabled
         * and either this value is true, or this value is unset and
         * {@code quarkus.micrometer.registry-enabled-default} is true.
         */
        @ConfigItem
        public Optional<Boolean> enabled;

        @Override
        public Optional<Boolean> getEnabled() {
            return enabled;
        }

        /**
         * By default, this extension will create a Datadog MeterRegistry instance.
         * <p>
         * Use this attribute to veto the creation of the default Datadog MeterRegistry.
         */
        @ConfigItem(defaultValue = "true")
        public boolean defaultRegistry;

        @Override
        public String toString() {
            return this.getClass().getSimpleName()
                    + "{enabled=" + enabled
                    + ",defaultRegistry=" + defaultRegistry
                    + '}';
        }
    }

    /**
     * Runtime configuration for Datadog MeterRegistry
     */
    @ConfigRoot(name = "micrometer.export.datadog", phase = ConfigPhase.RUN_TIME)
    public static class DatadogRuntimeConfig {
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
        @ConfigItem(name = ConfigItem.PARENT)
        Map<String, String> datadog;
    }
}
