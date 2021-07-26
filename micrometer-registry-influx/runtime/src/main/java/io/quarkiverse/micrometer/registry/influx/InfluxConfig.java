package io.quarkiverse.micrometer.registry.influx;

import java.util.Map;
import java.util.Optional;

import io.quarkus.micrometer.runtime.config.MicrometerConfig;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

public class InfluxConfig {

    @ConfigRoot(name = "micrometer.export.influx", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
    public static class InfluxBuildConfig implements MicrometerConfig.CapabilityEnabled {
        /**
         * Support for export to InfluxDB.
         * <p>
         * Support for InfluxDB will be enabled if Micrometer
         * support is enabled, the InfluxDB registry extension is enabled
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
         * By default, this extension will create a InfluxDB MeterRegistry instance.
         * <p>
         * Use this attribute to veto the creation of the default InfluxDB MeterRegistry.
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
     * Runtime configuration for InfluxDB
     */
    @ConfigRoot(name = "micrometer.export.influx", phase = ConfigPhase.RUN_TIME)
    public static class InfluxRuntimeConfig {

        // @formatter:off
        /**
         * InfluxDB registry configuration properties.
         *
         * A property source for configuration of the InfluxDB MeterRegistry,
         * see https://micrometer.io/docs/registry/influx.
         *
         * Available values:
         *
         * [cols=2]
         * !===
         * h!Property=Default
         * h!Description
         *
         * !`uri=http://localhost:8086`
         * !The url of the InfluxDB Endpoint.
         *
         * !`apiVersion=V1|V2`
         * !The API Version of the InfluxDB Endpoint.
         *
         * !`org=ORGNAME`
         * !The Organization of the InfluxDB Endpoint.
         *
         * !`userName=USERNAME`
         * !The userName of the InfluxDB Endpoint.
         *
         * !`token=TOKEN`
         * !The authentication token of the InfluxDB Endpoint.
         *
         * !`bucket=BUCKET`
         * !The bucket of the InfluxDB Endpoint.
         *
         * !`db=DATABASE`
         * !The database name of the InfluxDB Endpoint.
         *
         * !`step=1m`
         * !The interval at which metrics are sent to InfluxDB Monitoring. The default is 1 minute.
         * !===
         *
         * Other Micrometer configuration attributes can also be specified.
         *
         * As mentioned in the Micrometer InfluxDB documentation, if you want to customize the metrics
         * sink, do so by providing your own `InfluxDBMeterRegistry` instance using a CDI `@Produces`
         * method.
         *
         * @asciidoclet
         */
        // @formatter:on
        @ConfigItem(name = ConfigItem.PARENT)
        Map<String, String> influxdb;
    }
}
