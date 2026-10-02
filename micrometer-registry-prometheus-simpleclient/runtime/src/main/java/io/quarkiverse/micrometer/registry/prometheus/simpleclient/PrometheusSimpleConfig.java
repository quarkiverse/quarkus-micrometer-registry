package io.quarkiverse.micrometer.registry.prometheus.simpleclient;

import java.util.Map;
import java.util.Optional;

import io.quarkus.micrometer.runtime.config.MicrometerConfig;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithParentName;

public interface PrometheusSimpleConfig {
    @ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
    @ConfigMapping(prefix = "quarkus.micrometer.export.prometheus.simpleclient")
    public interface PrometheusSimpleClientBuildConfig extends MicrometerConfig.CapabilityEnabled {
        /**
         * Support for export to Prometheus SimpleClient.
         * <p>
         * Support for Prometheus Simple will be enabled if Micrometer
         * support is enabled, the Prometheus Simple registry extension is enabled
         * and either this value is true, or this value is unset and
         * {@code quarkus.micrometer.registry-enabled-default} is true.
         */
        @Override
        Optional<Boolean> enabled();

        /**
         * By default, this extension will create a Prometheus Simple MeterRegistry instance.
         * <p>
         * Use this attribute to veto the creation of the default Prometheus Simple MeterRegistry.
         */
        @WithDefault("true")
        public boolean defaultRegistry();
    }

    /**
     * Runtime configuration for Prometheus Simple
     */
    @ConfigRoot(phase = ConfigPhase.RUN_TIME)
    @ConfigMapping(prefix = "quarkus.micrometer.export.prometheus.simpleclient")
    public interface PrometheusSimpleClientRuntimeConfig {

        // @formatter:off
        /**
         * Prometheus SimpleClient registry configuration properties.
         *
         *
         * @asciidoclet
         */
        // @formatter:on
        @WithParentName
        Map<String, String> prometheusSimpleClient();
    }

}
