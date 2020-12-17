package io.quarkiverse.micrometer.registry.azuremonitor.deployment;

import java.util.function.BooleanSupplier;

import org.jboss.logging.Logger;

import io.quarkiverse.micrometer.registry.azuremonitor.AzureMonitorConfig;
import io.quarkiverse.micrometer.registry.azuremonitor.AzureMonitorMeterRegistryProvider;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.pkg.steps.NativeBuild;
import io.quarkus.micrometer.deployment.MicrometerRegistryProviderBuildItem;
import io.quarkus.micrometer.runtime.MicrometerRecorder;
import io.quarkus.micrometer.runtime.config.MicrometerConfig;

/**
 * Add support for the Azure Monitor Meter Registry. Note that the registry may not
 * be enabled. Keep footprint light.
 */
public class AzureMonitorRegistryProcessor {
    private static final Logger log = Logger.getLogger(AzureMonitorRegistryProcessor.class);

    public static final String REGISTRY_CLASS_NAME = "io.micrometer.azuremonitor.AzureMonitorMeterRegistry";
    public static final Class<?> REGISTRY_CLASS = MicrometerRecorder.getClassForName(REGISTRY_CLASS_NAME);

    public static class AzureMonitorEnabled implements BooleanSupplier {
        MicrometerConfig mConfig;
        AzureMonitorConfig.AzureMonitorBuildConfig azureMonitorConfig;

        @Override
        public boolean getAsBoolean() {
            return REGISTRY_CLASS != null && mConfig.checkRegistryEnabledWithDefault(azureMonitorConfig);
        }
    }

    @BuildStep(onlyIf = { NativeBuild.class, AzureMonitorEnabled.class })
    public MicrometerRegistryProviderBuildItem nativeModeNotSupported() {
        log.info("The Azure Monitor meter registry does not support running in native mode.");
        return null;
    }

    @BuildStep(onlyIf = AzureMonitorEnabled.class, onlyIfNot = NativeBuild.class)
    public MicrometerRegistryProviderBuildItem createAzureMonitorRegistry(
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        // Add the AzureMonitor Registry Producer
        additionalBeans.produce(
                AdditionalBeanBuildItem.builder()
                        .addBeanClass(AzureMonitorMeterRegistryProvider.class)
                        .setUnremovable().build());

        // Include the AzureMonitorMeterRegistry in a possible CompositeMeterRegistry
        return new MicrometerRegistryProviderBuildItem(REGISTRY_CLASS);
    }
}
