package io.quarkiverse.micrometer.registry.stackdriver;

import java.util.Map;
import java.util.Optional;

import io.quarkus.micrometer.runtime.config.MicrometerConfig;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithParentName;

public interface StackdriverConfig {

    @ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
    @ConfigMapping(prefix = "quarkus.micrometer.export.stackdriver")
    public interface StackdriverBuildConfig extends MicrometerConfig.CapabilityEnabled {
        /**
         * Support for export to Stackdriver.
         * <p>
         * Support for Stackdriver will be enabled if Micrometer
         * support is enabled, the Stackdriver registry extension is enabled
         * and either this value is true, or this value is unset and
         * `quarkus.micrometer.registry-enabled-default` is true.
         * <p>
         * [NOTE]
         * ====
         * Stackdriver libraries do not yet support running in native mode.
         * The Stackdriver MeterRegistry will be automatically disabled
         * for native builds.
         * <p>
         * See https://github.com/grpc/grpc-java/issues/5460
         * ====
         *
         * @asciidoclet
         */
        @Override
        Optional<Boolean> enabled();

        /**
         * By default, this extension will create a Stackdriver MeterRegistry instance.
         * <p>
         * Use this attribute to veto the creation of the default Stackdriver MeterRegistry.
         */
        @WithDefault("true")
        boolean defaultRegistry();
    }

    @ConfigRoot(phase = ConfigPhase.RUN_TIME)
    @ConfigMapping(prefix = "quarkus.micrometer.export.stackdriver")
    public interface StackdriverRuntimeConfig {
        // @formatter:off
        /**
         * Stackdriver registry configuration properties.
         *
         * A property source for configuration of the Stackdriver MeterRegistry,
         * see https://micrometer.io/docs/registry/stackdriver.
         *
         * Available values:
         *
         * [cols=2]
         * !===
         * h!Property=Default
         * h!Description
         *
         * !`project-id=MY_PROJECT_ID`
         * !Define the project id used to push data to Stackdriver Monitoring
         *
         * !`publish=true`
         * !By default, gathered metrics will be published to Stackdriver when the MeterRegistry is enabled.
         * Use this attribute to selectively disable publication of metrics in some environments.
         *
         * !`step=1m`
         * !The interval at which metrics are sent to Stackdriver Monitoring. The default is 1 minute.
         * !===
         *
         * Other Micrometer configuration attributes can also be specified.
         *
         * @asciidoclet
         */
        // @formatter:on
        @WithParentName
        Map<String, String> stackdriver();
    }
}
