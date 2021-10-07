package io.quarkiverse.micrometer.registry.influx.deployment;

import java.util.function.BooleanSupplier;

import org.jboss.logging.Logger;

import io.micrometer.influx.InfluxApiVersion;
import io.quarkiverse.micrometer.registry.influx.ConditionalRegistryProducer;
import io.quarkiverse.micrometer.registry.influx.InfluxConfig;
import io.quarkiverse.micrometer.registry.influx.InfluxConfig.InfluxBuildConfig;
import io.quarkiverse.micrometer.registry.influx.InfluxMeterRegistryProvider;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
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
    void addClassesForNativeReflection(BuildProducer<ReflectiveClassBuildItem> reflectionClasses) {
        log.warn("native support for InfluxDB meter registery is EXPERIMENTAL!!! check reflectionClasses");

        reflectionClasses.produce(new ReflectiveClassBuildItem(true, true, InfluxApiVersion.class));
    }

    @BuildStep(onlyIf = InfluxRegistryEnabled.class)
    public MicrometerRegistryProviderBuildItem createInfluxDBRegistry(
            InfluxBuildConfig influxBuildConfig,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        // Add the general InfluxDB Producer (config, naming conventions, .. )
        AdditionalBeanBuildItem.Builder builder = AdditionalBeanBuildItem.builder()
                .setUnremovable()
                .addBeanClass(InfluxMeterRegistryProvider.class);

        if (influxBuildConfig.defaultRegistry) {
            // Only add this Registry Producer if the default registry is enabled
            builder.addBeanClass(ConditionalRegistryProducer.class);
        }

        additionalBeans.produce(builder.build());

        // Include the InfluxDBMeterRegistryProvider in a possible CompositeMeterRegistry
        return new MicrometerRegistryProviderBuildItem(REGISTRY_CLASS);
    }

}
