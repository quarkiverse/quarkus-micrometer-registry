package io.quarkiverse.micrometer.registry.otlp.deployment;

import java.util.function.BooleanSupplier;

import io.quarkiverse.micrometer.registry.otlp.ConditionalRegistryProducer;
import io.quarkiverse.micrometer.registry.otlp.OtlpConfig;
import io.quarkiverse.micrometer.registry.otlp.OtlpConfig.OtlpBuildConfig;
import io.quarkiverse.micrometer.registry.otlp.OtlpMeterRegistryProvider;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.micrometer.deployment.MicrometerRegistryProviderBuildItem;
import io.quarkus.micrometer.runtime.MicrometerRecorder;
import io.quarkus.micrometer.runtime.config.MicrometerConfig;

/**
 * Add support for the Otlp Meter Registry. Note that the registry may not
 * be enabled. Keep footprint light.
 */
public class OtlpRegistryProcessor {
    public static final String REGISTRY_CLASS_NAME = "io.micrometer.registry.otlp.OtlpMeterRegistry";
    public static final Class<?> REGISTRY_CLASS = MicrometerRecorder.getClassForName(REGISTRY_CLASS_NAME);

    public static class OtlpRegistryEnabled implements BooleanSupplier {
        MicrometerConfig mConfig;
        OtlpConfig.OtlpBuildConfig otlpConfig;

        @Override
        public boolean getAsBoolean() {
            return REGISTRY_CLASS != null
                    && QuarkusClassLoader.isClassPresentAtRuntime(REGISTRY_CLASS_NAME)
                    && mConfig.checkRegistryEnabledWithDefault(otlpConfig);
        }
    }

    @BuildStep(onlyIf = OtlpRegistryEnabled.class)
    public MicrometerRegistryProviderBuildItem createOtlpRegistry(
            OtlpBuildConfig otlpBuildConfig,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        // Add the general Otlp Producer (config, .. )
        AdditionalBeanBuildItem.Builder builder = AdditionalBeanBuildItem.builder()
                .setUnremovable()
                .addBeanClass(OtlpMeterRegistryProvider.class);

        if (otlpBuildConfig.defaultRegistry()) {
            // Only add this Registry Producer if the default registry is enabled
            builder.addBeanClass(ConditionalRegistryProducer.class);
        }

        additionalBeans.produce(builder.build());

        // Include the OtlpMeterRegistryProvider in a possible CompositeMeterRegistry
        return new MicrometerRegistryProviderBuildItem(REGISTRY_CLASS);
    }
}
