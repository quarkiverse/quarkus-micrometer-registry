package io.quarkiverse.micrometer.registry.prometheus.simpleclient.deployment;

import java.util.function.BooleanSupplier;

import io.quarkiverse.micrometer.registry.prometheus.simpleclient.ConditionalRegistryProducer;
import io.quarkiverse.micrometer.registry.prometheus.simpleclient.PrometheusSimpleConfig;
import io.quarkiverse.micrometer.registry.prometheus.simpleclient.PrometheusSimpleMeterRegistryProvider;
import io.quarkiverse.micrometer.registry.prometheus.simpleclient.exemplars.EmptyExemplarSamplerProvider;
import io.quarkiverse.micrometer.registry.prometheus.simpleclient.exemplars.OpentelemetryExemplarSamplerProvider;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.micrometer.deployment.MicrometerRegistryProviderBuildItem;
import io.quarkus.micrometer.deployment.export.PrometheusRegistryProcessor;
import io.quarkus.micrometer.runtime.MicrometerRecorder;
import io.quarkus.micrometer.runtime.config.MicrometerConfig;

public class PrometheusSimpleProcessor {

    static final String REGISTRY_CLASS_NAME = "io.micrometer.prometheus.PrometheusMeterRegistry";
    static final Class<?> REGISTRY_CLASS = MicrometerRecorder.getClassForName(REGISTRY_CLASS_NAME);

    public static class PrometheusSimpleClientEnabled implements BooleanSupplier {
        MicrometerConfig mConfig;
        PrometheusSimpleConfig.PrometheusSimpleClientBuildConfig config;

        @Override
        public boolean getAsBoolean() {
            return REGISTRY_CLASS != null
                    && QuarkusClassLoader.isClassPresentAtRuntime(REGISTRY_CLASS_NAME)
                    && mConfig.checkRegistryEnabledWithDefault(config);
        }
    }

    public static class TraceEnabled implements BooleanSupplier {
        @Override
        public boolean getAsBoolean() {
            return QuarkusClassLoader.isClassPresentAtRuntime("io.quarkus.opentelemetry.runtime.OpenTelemetryUtil");
        }
    }

    @BuildStep(onlyIf = { PrometheusRegistryProcessor.TraceEnabled.class })
    void registerOpentelemetryExemplarSamplerProvider(
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
        additionalBeans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClass(OpentelemetryExemplarSamplerProvider.class)
                .setUnremovable()
                .build());
    }

    @BuildStep(onlyIfNot = { PrometheusRegistryProcessor.TraceEnabled.class })
    void registerEmptyExamplarProvider(
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        additionalBeans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClass(EmptyExemplarSamplerProvider.class)
                .setUnremovable()
                .build());
    }

    @BuildStep(onlyIf = PrometheusSimpleClientEnabled.class)
    public void removeMicrometerExtensionBeans(BuildProducer<AdditionalBeanBuildItem> unnededBeans) {

        AdditionalBeanBuildItem.Builder builder = AdditionalBeanBuildItem.builder()
                .setRemovable()
                .addBeanClasses(io.quarkus.micrometer.runtime.export.exemplars.OpentelemetryExemplarSamplerProvider.class,
                        io.quarkus.micrometer.runtime.export.exemplars.EmptyExemplarSamplerProvider.class);

        unnededBeans.produce(builder.build());
    }

    @BuildStep(onlyIf = PrometheusSimpleClientEnabled.class)
    public MicrometerRegistryProviderBuildItem createPrometheusSimpleClientRegistry(
            PrometheusSimpleConfig.PrometheusSimpleClientBuildConfig buildConfig,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        // Add the general PrometheusSimpleClient Producer (config, naming conventions, .. )
        AdditionalBeanBuildItem.Builder builder = AdditionalBeanBuildItem.builder()
                .setUnremovable()
                .addBeanClass(PrometheusSimpleMeterRegistryProvider.class);

        if (buildConfig.defaultRegistry()) {
            // Only add this Registry Producer if the default registry is enabled
            builder.addBeanClass(ConditionalRegistryProducer.class);
        }

        additionalBeans.produce(builder.build());

        // Include the PrometheusSimpleClientMeterRegistryProvider in a possible CompositeMeterRegistry
        return new MicrometerRegistryProviderBuildItem(REGISTRY_CLASS);
    }

}
