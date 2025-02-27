package io.quarkiverse.micrometer.registry.statsd.deployment;

import java.util.function.BooleanSupplier;

import org.jboss.logging.Logger;

import io.quarkiverse.micrometer.registry.statsd.ConditionalRegistryProducer;
import io.quarkiverse.micrometer.registry.statsd.StatsdConfig;
import io.quarkiverse.micrometer.registry.statsd.StatsdConfig.StatsdBuildConfig;
import io.quarkiverse.micrometer.registry.statsd.StatsdMeterRegistryProvider;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.pkg.steps.NativeBuild;
import io.quarkus.micrometer.deployment.MicrometerRegistryProviderBuildItem;
import io.quarkus.micrometer.runtime.MicrometerRecorder;
import io.quarkus.micrometer.runtime.config.MicrometerConfig;

/**
 * Add support for the StatsD Meter Registry. Note that the registry may not
 * be available at deployment time for some projects: Avoid direct class
 * references.
 */
public class StatsdRegistryProcessor {
    private static final Logger log = Logger.getLogger(StatsdRegistryProcessor.class);

    public static final String REGISTRY_CLASS_NAME = "io.micrometer.statsd.StatsdMeterRegistry";
    public static final Class<?> REGISTRY_CLASS = MicrometerRecorder.getClassForName(REGISTRY_CLASS_NAME);

    public static class StatsdRegistryEnabled implements BooleanSupplier {
        MicrometerConfig mConfig;
        StatsdConfig.StatsdBuildConfig statsdConfig;

        @Override
        public boolean getAsBoolean() {
            return REGISTRY_CLASS != null
                    && QuarkusClassLoader.isClassPresentAtRuntime(REGISTRY_CLASS_NAME)
                    && mConfig.checkRegistryEnabledWithDefault(statsdConfig);
        }
    }

    @BuildStep(onlyIf = { NativeBuild.class, StatsdRegistryEnabled.class })
    public MicrometerRegistryProviderBuildItem nativeModeNotSupported() {
        log.info("The StatsD meter registry does not support running in native mode.");
        return null;
    }

    @BuildStep(onlyIf = StatsdRegistryEnabled.class, onlyIfNot = NativeBuild.class)
    public MicrometerRegistryProviderBuildItem createStatsdRegistry(
            StatsdBuildConfig statsdBuildConfig,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        // Add the general Statsd general Producer (config, naming conventions, .. )
        AdditionalBeanBuildItem.Builder builder = AdditionalBeanBuildItem.builder()
                .setUnremovable()
                .addBeanClass(StatsdMeterRegistryProvider.class);

        if (statsdBuildConfig.defaultRegistry()) {
            // Only add this Registry Producer if the default registry is enabled
            builder.addBeanClass(ConditionalRegistryProducer.class);
        }

        additionalBeans.produce(builder.build());

        // Include the StatsdMeterRegistryProvider in a possible CompositeMeterRegistry
        return new MicrometerRegistryProviderBuildItem(REGISTRY_CLASS);
    }

}
