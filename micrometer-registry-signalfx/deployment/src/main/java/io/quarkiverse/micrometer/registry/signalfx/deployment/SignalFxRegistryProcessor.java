package io.quarkiverse.micrometer.registry.signalfx.deployment;

import java.util.function.BooleanSupplier;

import org.jboss.logging.Logger;

import io.quarkiverse.micrometer.registry.signalfx.ConditionalRegistryProducer;
import io.quarkiverse.micrometer.registry.signalfx.SignalFxConfig;
import io.quarkiverse.micrometer.registry.signalfx.SignalFxConfig.SignalFxBuildConfig;
import io.quarkiverse.micrometer.registry.signalfx.SignalFxMeterRegistryProvider;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.pkg.steps.NativeBuild;
import io.quarkus.micrometer.deployment.MicrometerRegistryProviderBuildItem;
import io.quarkus.micrometer.runtime.MicrometerRecorder;
import io.quarkus.micrometer.runtime.config.MicrometerConfig;

/**
 * Add support for the SignalFx Meter Registry. Note that the registry may not
 * be enabled. Keep footprint light.
 */
public class SignalFxRegistryProcessor {
    private static final Logger log = Logger.getLogger(SignalFxRegistryProcessor.class);

    public static final String REGISTRY_CLASS_NAME = "io.micrometer.signalfx.SignalFxMeterRegistry";
    public static final Class<?> REGISTRY_CLASS = MicrometerRecorder.getClassForName(REGISTRY_CLASS_NAME);

    public static class SignalFxRegistryEnabled implements BooleanSupplier {
        MicrometerConfig mConfig;
        SignalFxConfig.SignalFxBuildConfig signalFxConfig;

        @Override
        public boolean getAsBoolean() {
            return mConfig.checkRegistryEnabledWithDefault(signalFxConfig);
        }
    }

    @BuildStep(onlyIf = { NativeBuild.class, SignalFxRegistryEnabled.class })
    public MicrometerRegistryProviderBuildItem nativeModeNotSupported() {
        log.info("The SignalFx meter registry does not support running in native mode.");
        return null;
    }

    @BuildStep(onlyIf = SignalFxRegistryEnabled.class, onlyIfNot = NativeBuild.class)
    public MicrometerRegistryProviderBuildItem createSignalFxRegistry(
            SignalFxBuildConfig signalFxBuildConfig,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        // Add the general SignalFx general Producer (config, naming conventions, .. )
        AdditionalBeanBuildItem.Builder builder = AdditionalBeanBuildItem.builder()
                .setUnremovable()
                .addBeanClass(SignalFxMeterRegistryProvider.class);

        if (signalFxBuildConfig.defaultRegistry) {
            // Only add this Registry Producer if the default registry is enabled
            builder.addBeanClass(ConditionalRegistryProducer.class);
        }

        additionalBeans.produce(builder.build());

        // Include the SignalFxMeterRegistryProvider in a possible CompositeMeterRegistry
        return new MicrometerRegistryProviderBuildItem(REGISTRY_CLASS);
    }
}
