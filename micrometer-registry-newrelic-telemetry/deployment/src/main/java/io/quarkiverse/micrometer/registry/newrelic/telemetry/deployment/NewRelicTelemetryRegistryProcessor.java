package io.quarkiverse.micrometer.registry.newrelic.telemetry.deployment;

import java.util.function.BooleanSupplier;

import org.jboss.logging.Logger;

import io.quarkiverse.micrometer.registry.newrelic.telemetry.ConditionalRegistryProducer;
import io.quarkiverse.micrometer.registry.newrelic.telemetry.NewRelicConfig;
import io.quarkiverse.micrometer.registry.newrelic.telemetry.NewRelicMeterRegistryProvider;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
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
public class NewRelicTelemetryRegistryProcessor {
    private static final Logger log = Logger.getLogger(NewRelicTelemetryRegistryProcessor.class);

    static final String REGISTRY_CLASS_NAME = "com.newrelic.telemetry.micrometer.NewRelicRegistry";
    // This removes a hard dependency to the foreign Micrometer(old) or NewRelic (new) lib.
    static final Class<?> REGISTRY_CLASS = MicrometerRecorder.getClassForName(REGISTRY_CLASS_NAME);

    public static class NewRelicEnabled implements BooleanSupplier {
        MicrometerConfig mConfig;
        NewRelicConfig.NewRelicBuildConfig newRelicConfig;

        public boolean getAsBoolean() {
            return REGISTRY_CLASS != null
                    && QuarkusClassLoader.isClassPresentAtRuntime(REGISTRY_CLASS_NAME)
                    && mConfig.checkRegistryEnabledWithDefault(newRelicConfig);
        }
    }

    @BuildStep(onlyIf = { NativeBuild.class, NewRelicEnabled.class })
    public ExtensionSslNativeSupportBuildItem enableSSLSupport() {
        log.info("The New Relic meter registry does not support running in native mode.");
        return null;
    }

    @BuildStep(onlyIf = NewRelicEnabled.class, onlyIfNot = NativeBuild.class)
    protected MicrometerRegistryProviderBuildItem createNewRelicRegistry(
            CombinedIndexBuildItem index,
            NewRelicConfig.NewRelicBuildConfig newRelicBuildConfig,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        // Add the general New Relic general Producer (config, naming conventions, .. )
        AdditionalBeanBuildItem.Builder builder = AdditionalBeanBuildItem.builder()
                .setUnremovable()
                .addBeanClass(NewRelicMeterRegistryProvider.class);

        if (newRelicBuildConfig.defaultRegistry()) {
            // Only add this Registry Producer if the default registry is enabled
            builder.addBeanClass(ConditionalRegistryProducer.class);
        }

        additionalBeans.produce(builder.build());

        // Include the NewRelicMeterRegistry in a possible CompositeMeterRegistry
        return new MicrometerRegistryProviderBuildItem(REGISTRY_CLASS);
    }
}
