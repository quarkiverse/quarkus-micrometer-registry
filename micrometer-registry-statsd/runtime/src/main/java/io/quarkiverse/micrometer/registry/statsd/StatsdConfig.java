package io.quarkiverse.micrometer.registry.statsd;

import java.util.Map;
import java.util.Optional;

import io.quarkus.micrometer.runtime.config.MicrometerConfig;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

public class StatsdConfig {

    @ConfigRoot(name = "micrometer.export.statsd", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
    public static class StatsdBuildConfig implements MicrometerConfig.CapabilityEnabled {
        /**
         * Support for export to StatsD.
         * <p>
         * Support for StatsD will be enabled if Micrometer
         * support is enabled, the Statsd registry extension is enabled
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
         * By default, this extension will create a StatsD MeterRegistry instance.
         * <p>
         * Use this attribute to veto the creation of the default StatsD MeterRegistry.
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
     * Runtime configuration for StatsD
     */
    @ConfigRoot(name = "micrometer.export.statsd", phase = ConfigPhase.RUN_TIME)
    public static class StatsdRuntimeConfig {

        // @formatter:off
        /**
         * StatsD registry configuration properties.
         *
         * A property source for configuration of the StatsD MeterRegistry,
         * see https://micrometer.io/docs/registry/statsD.
         *
         * Available values:
         *
         * [cols=2]
         * !===
         * h!Property=Default
         * h!Description
         *
         * !`flavor=datadog`
         * !Specify the flavor of the StatsD line protocol. The original StatsD line protocol
         * specification is `etsy`. The default value is `datadog`.
         *
         * !`host=localhost`
         * !The host name of the StatsD agent.
         *
         * !`maxPacketLength=1400`
         * !Adjust the packet length to keep the payload within your network's MTU.
         *
         * !`port=8125`
         * !The port of the StatsD agent`.
         *
         * !`protocol=UDP`
         * !The protocol of the connection to the agent (UDP or TCP).
         *
         * !`publish=true`
         * !By default, gathered metrics will be published to StatsD when the MeterRegistry is enabled.
         * Use this attribute to selectively disable publication of metrics in some environments.
         *
         * !`step=1m`
         * !The interval at which metrics are sent to StatsD Monitoring. The default is 1 minute.
         * !===
         *
         * Other Micrometer configuration attributes can also be specified.
         *
         * As mentioned in the Micrometer StatsD documentation, if you want to customize the metrics
         * sink, do so by providing your own `StatsDMeterRegistry` instance using a CDI `@Produces`
         * method.
         *
         * @asciidoclet
         */
        // @formatter:on
        @ConfigItem(name = ConfigItem.PARENT)
        Map<String, String> statsd;
    }
}
