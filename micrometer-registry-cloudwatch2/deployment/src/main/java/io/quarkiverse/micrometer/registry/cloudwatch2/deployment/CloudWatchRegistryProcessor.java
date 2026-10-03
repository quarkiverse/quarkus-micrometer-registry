package io.quarkiverse.micrometer.registry.cloudwatch2.deployment;

import java.util.function.BooleanSupplier;

import io.quarkiverse.micrometer.registry.cloudwatch2.CloudWatchConfig;
import io.quarkiverse.micrometer.registry.cloudwatch2.CloudWatchConfig.CloudWatchBuildConfig;
import io.quarkiverse.micrometer.registry.cloudwatch2.CloudWatchMeterRegistryProvider;
import io.quarkiverse.micrometer.registry.cloudwatch2.ConditionalRegistryProducer;
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
 * Add support for the AWS CloudWatch Meter Registry. Note that the registry may not
 * be enabled. Keep footprint light.
 */
public class CloudWatchRegistryProcessor {
    static final String REGISTRY_CLASS_NAME = "io.micrometer.cloudwatch2.CloudWatchMeterRegistry";
    static final Class<?> REGISTRY_CLASS = MicrometerRecorder.getClassForName(REGISTRY_CLASS_NAME);

    public static class CloudWatchEnabled implements BooleanSupplier {
        MicrometerConfig mConfig;
        CloudWatchConfig.CloudWatchBuildConfig cloudWatchConfig;

        public boolean getAsBoolean() {
            return REGISTRY_CLASS != null
                    && QuarkusClassLoader.isClassPresentAtRuntime(REGISTRY_CLASS_NAME)
                    && mConfig.checkRegistryEnabledWithDefault(cloudWatchConfig);
        }
    }

    @BuildStep(onlyIf = { NativeBuild.class })
    public ExtensionSslNativeSupportBuildItem enableSSLSupport() {
        return new ExtensionSslNativeSupportBuildItem("micrometer-registry-cloudwatch2");
    }

    @BuildStep(onlyIf = CloudWatchEnabled.class)
    protected MicrometerRegistryProviderBuildItem createCloudWatchRegistry(
            CombinedIndexBuildItem index,
            CloudWatchBuildConfig cloudWatchBuildConfig,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        // Add the general CloudWatch Producer (config, client, naming conventions, .. )
        AdditionalBeanBuildItem.Builder builder = AdditionalBeanBuildItem.builder()
                .setUnremovable()
                .addBeanClass(CloudWatchMeterRegistryProvider.class);

        if (cloudWatchBuildConfig.defaultRegistry()) {
            // Only add this Registry Producer if the default registry is enabled
            builder.addBeanClass(ConditionalRegistryProducer.class);
        }

        additionalBeans.produce(builder.build());

        // Include the CloudWatchMeterRegistry in a possible CompositeMeterRegistry
        return new MicrometerRegistryProviderBuildItem(REGISTRY_CLASS);
    }
}
