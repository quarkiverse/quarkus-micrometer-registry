package io.quarkiverse.micrometer.registry.jmx.deployment;

import java.util.function.BooleanSupplier;

import org.jboss.logging.Logger;

import io.quarkiverse.micrometer.registry.jmx.JmxConfig;
import io.quarkiverse.micrometer.registry.jmx.JmxMeterRegistryProvider;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.pkg.steps.NativeBuild;
import io.quarkus.micrometer.deployment.MicrometerRegistryProviderBuildItem;
import io.quarkus.micrometer.runtime.MicrometerRecorder;
import io.quarkus.micrometer.runtime.config.MicrometerConfig;

/**
 * Add support for the Jmx Meter Registry. Note that the registry may not
 * be enabled. Keep footprint light.
 */
public class JmxRegistryProcessor {
    private static final Logger log = Logger.getLogger(JmxRegistryProcessor.class);

    static final String REGISTRY_CLASS_NAME = "io.micrometer.jmx.JmxMeterRegistry";
    static final Class<?> REGISTRY_CLASS = MicrometerRecorder.getClassForName(REGISTRY_CLASS_NAME);

    public static class JmxEnabled implements BooleanSupplier {
        MicrometerConfig mConfig;
        JmxConfig.JmxBuildConfig jmxConfig;

        public boolean getAsBoolean() {
            return mConfig.checkRegistryEnabledWithDefault(jmxConfig);
        }
    }

    @BuildStep(onlyIf = { NativeBuild.class, JmxEnabled.class })
    protected MicrometerRegistryProviderBuildItem createJmxRegistry(CombinedIndexBuildItem index) {
        log.info("JMX Meter Registry does not support running in native mode.");
        return null;
    }

    /** Jmx does not work with GraalVM */
    @BuildStep(onlyIf = JmxEnabled.class, onlyIfNot = NativeBuild.class)
    protected MicrometerRegistryProviderBuildItem createJmxRegistry(CombinedIndexBuildItem index,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        // Add the Jmx Registry Producer
        additionalBeans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClass(JmxMeterRegistryProvider.class)
                .setUnremovable().build());

        // Include the JmxMeterRegistry in a possible CompositeMeterRegistry
        return new MicrometerRegistryProviderBuildItem(REGISTRY_CLASS);
    }
}
