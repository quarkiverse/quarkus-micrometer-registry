package io.quarkiverse.micrometer.registry.jmx;

import java.util.Map;
import java.util.Optional;

import io.quarkus.micrometer.runtime.config.MicrometerConfig;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithParentName;

public interface JmxConfig {

    @ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
    @ConfigMapping(prefix = "quarkus.micrometer.export.jmx")
    public interface JmxBuildConfig extends MicrometerConfig.CapabilityEnabled {
        /**
         * Support for export to JMX
         * <p>
         * Support for JMX will be enabled if Micrometer
         * support is enabled, the JMX registry extension is enabled
         * and either this value is true, or this value is unset and
         * {@code quarkus.micrometer.registry-enabled-default} is true.
         */
        @Override
        Optional<Boolean> enabled();

        /**
         * By default, this extension will create a JMX MeterRegistry instance.
         * <p>
         * Use this attribute to veto the creation of the default JMX MeterRegistry.
         */
        @WithDefault("true")
        boolean defaultRegistry();
    }

    /**
     * Runtime configuration for JMX MeterRegistry
     */
    @ConfigRoot(phase = ConfigPhase.RUN_TIME)
    @ConfigMapping(prefix = "quarkus.micrometer.export.jmx")
    public interface JmxRuntimeConfig {
        // @formatter:off
        /**
         * JMX registry configuration properties.
         *
         * A property source for configuration of the JMX MeterRegistry,
         * see https://micrometer.io/docs/registry/jmx.
         *
         * @asciidoclet
         */
        // @formatter:on
        @WithParentName
        Map<String, String> jmx();
    }

}
