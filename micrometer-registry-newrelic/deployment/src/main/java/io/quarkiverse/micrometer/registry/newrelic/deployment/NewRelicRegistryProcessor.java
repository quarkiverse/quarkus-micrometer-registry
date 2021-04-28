package io.quarkiverse.micrometer.registry.newrelic.deployment;

import java.util.function.BooleanSupplier;

import io.quarkiverse.micrometer.registry.newrelic.NewRelicConfig;
import io.quarkiverse.micrometer.registry.newrelic.NewRelicMeterRegistryProvider;
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
 * Add support for the New Relic Meter Registry. Note that the registry may not
 * be enabled. Keep footprint light.
 */
public class NewRelicRegistryProcessor {
    static final String REGISTRY_CLASS_NAME = "io.micrometer.newrelic.NewRelicMeterRegistry";
    static final Class<?> REGISTRY_CLASS = MicrometerRecorder.getClassForName(REGISTRY_CLASS_NAME);

    public static class NewRelicEnabled implements BooleanSupplier {
        MicrometerConfig mConfig;
        NewRelicConfig.NewRelicBuildConfig newRelicConfig;

        public boolean getAsBoolean() {
            return mConfig.checkRegistryEnabledWithDefault(newRelicConfig);
        }
    }

    @BuildStep(onlyIf = { NativeBuild.class })
    public ExtensionSslNativeSupportBuildItem enableSSLSupport() {
        return new ExtensionSslNativeSupportBuildItem("micrometer-registry-new-relic");
    }

    @BuildStep(onlyIf = NewRelicEnabled.class)
    protected MicrometerRegistryProviderBuildItem createNewRelicRegistry(CombinedIndexBuildItem index,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        // Add the New Relic Registry Producer
        additionalBeans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClass(NewRelicMeterRegistryProvider.class)
                .setUnremovable().build());

        // Include the NewRelicMeterRegistry in a possible CompositeMeterRegistry
        return new MicrometerRegistryProviderBuildItem(REGISTRY_CLASS);
    }
}
