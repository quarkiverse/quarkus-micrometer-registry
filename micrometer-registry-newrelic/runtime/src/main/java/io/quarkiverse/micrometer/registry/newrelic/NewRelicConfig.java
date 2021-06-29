package io.quarkiverse.micrometer.registry.newrelic;

import java.util.Map;
import java.util.Optional;

import io.quarkus.micrometer.runtime.config.MicrometerConfig;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

public class NewRelicConfig {

    @ConfigRoot(name = "micrometer.export.newrelic", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
    public static class NewRelicBuildConfig implements MicrometerConfig.CapabilityEnabled {
        /**
         * Support for export to New Relic
         * <p>
         * Support for New Relic will be enabled if Micrometer
         * support is enabled, the New Relic registry extension is enabled
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
         * By default, this extension will create a New Relic MeterRegistry instance.
         * <p>
         * Use this attribute to veto the creation of the default New Relic MeterRegistry.
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
     * Runtime configuration for New Relic MeterRegistry
     */
    @ConfigRoot(name = "micrometer.export.newrelic", phase = ConfigPhase.RUN_TIME)
    public static class NewRelicRuntimeConfig {
        // @formatter:off
        /**
         * New Relic MeterRegistry configuration properties.
         *
         * A property source for configuration of the New Relic MeterRegistry to push
         * metrics using the New Relic API, see https://micrometer.io/docs/registry/new-relic.
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
         * !`accountId=YOUR_ACCOUNT_ID`
         * !Define the account ID used to push data using the New Relic API
         *
         * !`publish=true`
         * !By default, gathered metrics will be published to New Relic when the MeterRegistry is enabled.
         * Use this attribute to selectively disable publication of metrics in some environments.
         *
         * !`step=1m`
         * !The interval at which metrics are sent to New Relic. The default is 1 minute.
         * !===
         *
         * Other Micrometer configuration attributes can also be specified.
         *
         * @asciidoclet
         */
        // @formatter:on
        @ConfigItem(name = ConfigItem.PARENT)
        Map<String, String> newrelic;
    }
}
