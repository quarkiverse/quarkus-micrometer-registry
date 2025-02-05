package io.quarkiverse.micrometer.registry.azuremonitor;

import java.util.Map;
import java.util.Optional;

import io.quarkus.micrometer.runtime.config.MicrometerConfig;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithParentName;

public interface AzureMonitorConfig {

    @ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
    @ConfigMapping(prefix = "quarkus.micrometer.export.azuremonitor")
    public interface AzureMonitorBuildConfig extends MicrometerConfig.CapabilityEnabled {
        /**
         * Support for export to AzureMonitor.
         * <p>
         * Support for AzureMonitor will be enabled if Micrometer
         * support is enabled, the Azure Monitor registry extension is enabled
         * and either this value is true, or this value is unset and
         * {@code quarkus.micrometer.registry-enabled-default} is true.
         */
        @Override
        Optional<Boolean> enabled();

        /**
         * By default, this extension will create a AzureMonitor MeterRegistry instance.
         * <p>
         * Use this attribute to veto the creation of the default AzureMonitor MeterRegistry.
         */
        @WithDefault("true")
        boolean defaultRegistry();
    }

    /**
     * Runtime configuration for Azure Monitor MeterRegistry
     */
    @ConfigRoot(phase = ConfigPhase.RUN_TIME)
    @ConfigMapping(prefix = "quarkus.micrometer.export.azuremonitor")
    public interface AzureMonitorRuntimeConfig {
        // @formatter:off
        /**
         * Azure Monitor registry configuration properties.
         *
         * A property source for configuration of the AzureMonitor MeterRegistry.
         *
         * Available values:
         *
         * [cols=2]
         * !===
         * h!Property=Default
         * h!Description
         *
         * !`instrumentation-key`
         * !Define the instrumentation key used to push data to Azure Insights Monitor
         *
         * !`publish=true`
         * !By default, gathered metrics will be published to Azure Monitor when the MeterRegistry is enabled.
         * Use this attribute to selectively disable publication of metrics in some environments.
         *
         * !===
         *
         * Other Micrometer configuration attributes can also be specified.
         *
         * @asciidoclet
         */
        // @formatter:on
        @WithParentName
        Map<String, String> azuremonitor();
    }
}
