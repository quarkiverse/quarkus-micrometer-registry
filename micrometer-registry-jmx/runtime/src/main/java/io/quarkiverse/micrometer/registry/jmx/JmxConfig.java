package io.quarkiverse.micrometer.registry.jmx;

import java.util.Map;
import java.util.Optional;

import io.quarkus.micrometer.runtime.config.MicrometerConfig;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

public class JmxConfig {

    @ConfigRoot(name = "micrometer.export.jmx", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
    public static class JmxBuildConfig implements MicrometerConfig.CapabilityEnabled {
        /**
         * Support for export to JMX
         * <p>
         * Support for JMX will be enabled if Micrometer
         * support is enabled, the JMX registry extension is enabled
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
         * By default, this extension will create a JMX MeterRegistry instance.
         * <p>
         * Use this attribute to veto the creation of the default JMX MeterRegistry.
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
     * Runtime configuration for JMX MeterRegistry
     */
    @ConfigRoot(name = "micrometer.export.jmx", phase = ConfigPhase.RUN_TIME)
    public static class JmxRuntimeConfig {
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
        @ConfigItem(name = ConfigItem.PARENT)
        Map<String, String> jmx;
    }

}
