package io.quarkiverse.micrometer.registry.datadog.deployment;

import java.util.function.BooleanSupplier;

import io.quarkiverse.micrometer.registry.datadog.ConditionalRegistryProducer;
import io.quarkiverse.micrometer.registry.datadog.DatadogConfig;
import io.quarkiverse.micrometer.registry.datadog.DatadogConfig.DatadogBuildConfig;
import io.quarkiverse.micrometer.registry.datadog.DatadogMeterRegistryProvider;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.ExtensionSslNativeSupportBuildItem;
import io.quarkus.deployment.pkg.steps.NativeBuild;
import io.quarkus.micrometer.deployment.MicrometerRegistryProviderBuildItem;
import io.quarkus.micrometer.runtime.MicrometerRecorder;
import io.quarkus.micrometer.runtime.config.MicrometerConfig;

/**
 * Add support for the Datadog Meter Registry. Note that the registry may not
 * be enabled. Keep footprint light.
 */
public class DatadogRegistryProcessor {
    static final String REGISTRY_CLASS_NAME = "io.micrometer.datadog.DatadogMeterRegistry";
    static final Class<?> REGISTRY_CLASS = MicrometerRecorder.getClassForName(REGISTRY_CLASS_NAME);

    public static class DatadogEnabled implements BooleanSupplier {
        MicrometerConfig mConfig;
        DatadogConfig.DatadogBuildConfig datadogConfig;

        public boolean getAsBoolean() {
            return mConfig.checkRegistryEnabledWithDefault(datadogConfig);
        }
    }

    @BuildStep(onlyIf = { NativeBuild.class })
    public ExtensionSslNativeSupportBuildItem enableSSLSupport() {
        return new ExtensionSslNativeSupportBuildItem("micrometer-registry-datadog");
    }

    @BuildStep(onlyIf = DatadogEnabled.class)
    protected MicrometerRegistryProviderBuildItem createDatadogRegistry(
            CombinedIndexBuildItem index,
            DatadogBuildConfig datadogBuildConfig,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        // Add the general Datadog Producer (config, naming conventions, .. )
        AdditionalBeanBuildItem.Builder builder = AdditionalBeanBuildItem.builder()
                .setUnremovable()
                .addBeanClass(DatadogMeterRegistryProvider.class);

        if (datadogBuildConfig.defaultRegistry) {
            // Only add this Registry Producer if the default registry is enabled
            builder.addBeanClass(ConditionalRegistryProducer.class);
        }

        additionalBeans.produce(builder.build());

        // Include the DatadogMeterRegistry in a possible CompositeMeterRegistry
        return new MicrometerRegistryProviderBuildItem(REGISTRY_CLASS);
    }
}
