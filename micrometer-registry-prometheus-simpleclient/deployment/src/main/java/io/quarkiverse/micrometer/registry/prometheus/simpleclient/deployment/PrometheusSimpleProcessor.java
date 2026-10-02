package io.quarkiverse.micrometer.registry.prometheus.simpleclient.deployment;

import java.util.function.BooleanSupplier;

import io.quarkiverse.micrometer.registry.prometheus.simpleclient.ConditionalRegistryProducer;
import io.quarkiverse.micrometer.registry.prometheus.simpleclient.PrometheusSimpleConfig;
import io.quarkiverse.micrometer.registry.prometheus.simpleclient.PrometheusSimpleMeterRegistryProvider;
import io.quarkiverse.micrometer.registry.prometheus.simpleclient.PrometheusSimpleRecorder;
import io.quarkiverse.micrometer.registry.prometheus.simpleclient.exemplars.EmptyExemplarSamplerProvider;
import io.quarkiverse.micrometer.registry.prometheus.simpleclient.exemplars.OpenTelemetryExemplarContextUnwrapper;
import io.quarkiverse.micrometer.registry.prometheus.simpleclient.exemplars.OpentelemetryExemplarSamplerProvider;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.BuildSteps;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.micrometer.deployment.MicrometerRegistryProviderBuildItem;
import io.quarkus.micrometer.deployment.export.RegistryBuildItem;
import io.quarkus.micrometer.runtime.MicrometerRecorder;
import io.quarkus.micrometer.runtime.config.MicrometerConfig;
import io.quarkus.micrometer.runtime.config.PrometheusConfigGroup;
import io.quarkus.micrometer.runtime.export.PrometheusRecorder;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.runtime.management.ManagementInterfaceBuildTimeConfig;

@BuildSteps(onlyIf = PrometheusSimpleProcessor.PrometheusSimpleClientEnabled.class)
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

    @BuildStep(onlyIf = TraceEnabled.class)
    void registerOpentelemetryExemplarSamplerProvider(
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
        additionalBeans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClass(OpentelemetryExemplarSamplerProvider.class)
                .addBeanClass(OpenTelemetryExemplarContextUnwrapper.class)
                .setUnremovable()
                .build());
    }

    // No context unwrapper here: Quarkus core already registers a no-op one
    @BuildStep(onlyIfNot = TraceEnabled.class)
    void registerEmptyExamplarProvider(
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
        additionalBeans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClass(EmptyExemplarSamplerProvider.class)
                .setUnremovable()
                .build());
    }

    @BuildStep
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

    /**
     * Quarkus core only exposes the scrape endpoint for the new Prometheus client registry
     * ({@code io.micrometer.prometheusmetrics}), so the simpleclient registry needs its own handler.
     * Route setup and the fallback handler are reused from core's {@link PrometheusRecorder}.
     */
    @BuildStep
    @Record(value = ExecutionTime.STATIC_INIT)
    void createPrometheusRoute(BuildProducer<RouteBuildItem> routes,
            BuildProducer<RegistryBuildItem> registries,
            MicrometerConfig mConfig,
            NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
            ManagementInterfaceBuildTimeConfig managementBuildTimeConfig,
            LaunchModeBuildItem launchModeBuildItem,
            PrometheusRecorder recorder,
            PrometheusSimpleRecorder simpleRecorder) {

        PrometheusConfigGroup pConfig = mConfig.export().prometheus();

        // Exact match for resources matched to the root path
        routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                .management()
                .routeFunction(pConfig.path(), recorder.route())
                .routeConfigKey("quarkus.micrometer.export.prometheus.path")
                .handler(simpleRecorder.getHandler())
                .displayOnNotFoundPage("Metrics")
                .blockingRoute()
                .build());

        // Match paths that begin with the deployment path
        routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                .management()
                .routeFunction(pConfig.path() + (pConfig.path().endsWith("/") ? "*" : "/*"), recorder.route())
                .handler(simpleRecorder.getHandler())
                .blockingRoute()
                .build());

        // Fallback paths (for non text/plain requests)
        routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                .management()
                .routeFunction(pConfig.path(), recorder.fallbackRoute())
                .handler(recorder.getFallbackHandler())
                .build());
        routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                .management()
                .routeFunction(pConfig.path() + (pConfig.path().endsWith("/") ? "*" : "/*"), recorder.fallbackRoute())
                .handler(recorder.getFallbackHandler())
                .build());

        var path = nonApplicationRootPathBuildItem.resolveManagementPath(pConfig.path(),
                managementBuildTimeConfig, launchModeBuildItem);
        registries.produce(new RegistryBuildItem("Prometheus", path));
    }
}
