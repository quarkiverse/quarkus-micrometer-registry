package io.quarkiverse.micrometer.registry.stackdriver.deployment;

import java.util.function.BooleanSupplier;

import org.jboss.logging.Logger;

import io.quarkiverse.micrometer.registry.stackdriver.ConditionalRegistryProducer;
import io.quarkiverse.micrometer.registry.stackdriver.StackdriverConfig;
import io.quarkiverse.micrometer.registry.stackdriver.StackdriverConfig.StackdriverBuildConfig;
import io.quarkiverse.micrometer.registry.stackdriver.StackdriverMeterRegistryProvider;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.micrometer.deployment.MicrometerRegistryProviderBuildItem;
import io.quarkus.micrometer.runtime.MicrometerRecorder;
import io.quarkus.micrometer.runtime.config.MicrometerConfig;

/**
 * Add support for the Stackdriver Meter Registry. Note that the registry may not
 * be available at deployment time for some projects: Avoid direct class
 * references.
 */
public class StackdriverRegistryProcessor {
    private static final Logger log = Logger.getLogger(StackdriverRegistryProcessor.class);

    public static final String REGISTRY_CLASS_NAME = "io.micrometer.stackdriver.StackdriverMeterRegistry";
    public static final Class<?> REGISTRY_CLASS = MicrometerRecorder.getClassForName(REGISTRY_CLASS_NAME);

    public static class StackdriverEnabled implements BooleanSupplier {
        MicrometerConfig mConfig;
        StackdriverConfig.StackdriverBuildConfig stackdriverConfig;

        public boolean getAsBoolean() {
            return mConfig.checkRegistryEnabledWithDefault(stackdriverConfig);
        }
    }

    @BuildStep(onlyIf = StackdriverEnabled.class)
    public MicrometerRegistryProviderBuildItem createStackdriverRegistry(
            CombinedIndexBuildItem index,
            StackdriverBuildConfig stackdriverBuildConfig,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        // Add the general Stackdriver general Producer (config, naming conventions, .. )
        AdditionalBeanBuildItem.Builder builder = AdditionalBeanBuildItem.builder()
                .setUnremovable()
                .addBeanClass(StackdriverMeterRegistryProvider.class);

        if (stackdriverBuildConfig.defaultRegistry) {
            // Only add this Registry Producer if the default registry is enabled
            builder.addBeanClass(ConditionalRegistryProducer.class);
        }

        additionalBeans.produce(builder.build());

        // Include the StackdriverMeterRegistry in a possible CompositeMeterRegistry
        return new MicrometerRegistryProviderBuildItem(REGISTRY_CLASS);
    }
}
