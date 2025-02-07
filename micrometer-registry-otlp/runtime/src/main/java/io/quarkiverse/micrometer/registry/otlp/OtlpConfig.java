package io.quarkiverse.micrometer.registry.otlp;

import java.util.Map;
import java.util.Optional;

import io.quarkus.micrometer.runtime.config.MicrometerConfig;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

public class OtlpConfig {

    @ConfigRoot(name = "micrometer.export.otlp", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
    public static class OtlpBuildConfig implements MicrometerConfig.CapabilityEnabled {
        /**
         * Support for export to OTLP.
         * <p>
         * Support for OTLP will be enabled if Micrometer
         * support is enabled, the OTLP registry extension is enabled
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
         * By default, this extension will create a OTLP MeterRegistry instance.
         * <p>
         * Use this attribute to veto the creation of the default OTLP MeterRegistry.
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
     * Runtime configuration for OTLP MeterRegistry
     */
    @ConfigRoot(name = "micrometer.export.otlp", phase = ConfigPhase.RUN_TIME)
    public static class OtlpRuntimeConfig {
        // @formatter:off
        /**
         * OTLP registry configuration properties.
         *
         * A property source for configuration of the OTLP MeterRegistry,
         * see https://micrometer.io/docs/registry/otlp.
         *
         * Available values:
         *
         * [cols=2]
         * !===
         * h!Property=Default
         * h!Description
         *
         * !`url=string`
         * !Address to where metrics will be published.
         * Defaults to http://localhost:4318/v1/metrics
         *
         * !`batchSize=int`
         * !Number of ``Meters``s to include in a single payload sent to the backend.
         * Defaults to 10,000.
         *
         * !`step=1m`
         * !The interval at which metrics are sent. The default is 1 minute.
         *
         * !`resourceAttributes=list`
         * !A comma-separated list of attributes describing to be used as resource attributes, e.g. "key1=val1,key2=val2".
         * Defaults to empty. If not set it tries to use OTEL_RESOURCE_ATTRIBUTES and OTEL_SERVICE_NAME to automatically
         * build the resource attributes.
         * !===
         *
         * Other Micrometer configuration attributes can also be specified.
         *
         * @asciidoclet
         */
        // @formatter:on
        @ConfigItem(name = ConfigItem.PARENT)
        Map<String, String> otlp;
    }
}
