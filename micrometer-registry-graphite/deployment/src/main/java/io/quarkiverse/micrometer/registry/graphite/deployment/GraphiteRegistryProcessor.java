package io.quarkiverse.micrometer.registry.graphite.deployment;

import java.util.function.BooleanSupplier;

import io.quarkiverse.micrometer.registry.graphite.ConditionalRegistryProducer;
import io.quarkiverse.micrometer.registry.graphite.GraphiteConfig;
import io.quarkiverse.micrometer.registry.graphite.GraphiteConfig.GraphiteBuildConfig;
import io.quarkiverse.micrometer.registry.graphite.GraphiteMeterRegistryProvider;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.pkg.steps.NativeBuild;
import io.quarkus.micrometer.deployment.MicrometerRegistryProviderBuildItem;
import io.quarkus.micrometer.runtime.MicrometerRecorder;
import io.quarkus.micrometer.runtime.config.MicrometerConfig;

/**
 * Add support for the Graphite Meter Registry. Note that the registry may not
 * be enabled. Keep footprint light.
 */
public class GraphiteRegistryProcessor {
    public static final String REGISTRY_CLASS_NAME = "io.micrometer.graphite.GraphiteMeterRegistry";
    public static final Class<?> REGISTRY_CLASS = MicrometerRecorder.getClassForName(REGISTRY_CLASS_NAME);

    public static class GraphiteRegistryEnabled implements BooleanSupplier {
        MicrometerConfig mConfig;
        GraphiteConfig.GraphiteBuildConfig graphiteConfig;

        @Override
        public boolean getAsBoolean() {
            return REGISTRY_CLASS != null
                    && QuarkusClassLoader.isClassPresentAtRuntime(REGISTRY_CLASS_NAME)
                    && mConfig.checkRegistryEnabledWithDefault(graphiteConfig);
        }
    }

    @BuildStep(onlyIf = { NativeBuild.class, GraphiteRegistryEnabled.class })
    public void process(BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
        reflectiveClass.produce(ReflectiveClassBuildItem.builder("io.micrometer.graphite.GraphiteProtocol")
                .constructors(true)
                .methods(true)
                .fields(true)
                .build());

    }

    @BuildStep(onlyIf = GraphiteRegistryEnabled.class)
    public MicrometerRegistryProviderBuildItem createGraphiteRegistry(
            GraphiteBuildConfig graphiteBuildConfig,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        // Add the general Graphite Producer (config, naming conventions, .. )
        AdditionalBeanBuildItem.Builder builder = AdditionalBeanBuildItem.builder()
                .setUnremovable()
                .addBeanClass(GraphiteMeterRegistryProvider.class);

        if (graphiteBuildConfig.defaultRegistry) {
            // Only add this Registry Producer if the default registry is enabled
            builder.addBeanClass(ConditionalRegistryProducer.class);
        }

        additionalBeans.produce(builder.build());

        // Include the GraphiteMeterRegistryProvider in a possible CompositeMeterRegistry
        return new MicrometerRegistryProviderBuildItem(REGISTRY_CLASS);
    }
}
