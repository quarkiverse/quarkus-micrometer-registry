package io.quarkiverse.micrometer.registry.influx.deployment;

import java.util.function.BooleanSupplier;

import org.jboss.logging.Logger;

import io.quarkiverse.micrometer.registry.influx.InfluxConfig;
import io.quarkiverse.micrometer.registry.influx.InfluxMeterRegistryProvider;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.pkg.steps.NativeBuild;
import io.quarkus.micrometer.deployment.MicrometerRegistryProviderBuildItem;
import io.quarkus.micrometer.runtime.MicrometerRecorder;
import io.quarkus.micrometer.runtime.config.MicrometerConfig;

/**
 * Add support for the InfluxDB Meter Registry. Note that the registry may not
 * be available at deployment time for some projects: Avoid direct class
 * references.
 */
public class InfluxRegistryProcessor {
    private static final Logger log = Logger.getLogger(InfluxRegistryProcessor.class);

    public static final String REGISTRY_CLASS_NAME = "io.micrometer.influx.InfluxMeterRegistry";
    public static final Class<?> REGISTRY_CLASS = MicrometerRecorder.getClassForName(REGISTRY_CLASS_NAME);

    public static class InfluxRegistryEnabled implements BooleanSupplier {
        MicrometerConfig mConfig;
        InfluxConfig.InfluxBuildConfig influxConfig;

        @Override
        public boolean getAsBoolean() {
            return REGISTRY_CLASS != null && mConfig.checkRegistryEnabledWithDefault(influxConfig);
        }
    }

    @BuildStep(onlyIf = { NativeBuild.class, InfluxRegistryEnabled.class })
    public MicrometerRegistryProviderBuildItem nativeModeNotSupported() {
        log.info("The InfluxDB meter registry does not support running in native mode.");
        return null;
    }

    @BuildStep(onlyIf = InfluxRegistryEnabled.class, onlyIfNot = NativeBuild.class)
    public MicrometerRegistryProviderBuildItem createInfluxDBRegistry(BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        // Add the InfluxDB Registry Producer
        additionalBeans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClass(InfluxMeterRegistryProvider.class)
                .setUnremovable().build());

        // Include the InfluxDBMeterRegistryProvider in a possible CompositeMeterRegistry
        return new MicrometerRegistryProviderBuildItem(REGISTRY_CLASS);
    }

}
