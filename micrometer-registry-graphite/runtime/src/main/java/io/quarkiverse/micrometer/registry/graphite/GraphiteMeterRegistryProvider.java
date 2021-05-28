package io.quarkiverse.micrometer.registry.graphite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.Config;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.graphite.GraphiteConfig;
import io.micrometer.graphite.GraphiteDimensionalNameMapper;
import io.micrometer.graphite.GraphiteHierarchicalNameMapper;
import io.micrometer.graphite.GraphiteMeterRegistry;
import io.quarkus.arc.DefaultBean;
import io.quarkus.micrometer.runtime.export.ConfigAdapter;

@Singleton
public class GraphiteMeterRegistryProvider {
    static final String PREFIX = "quarkus.micrometer.export.graphite.";

    @Produces
    @Singleton
    @DefaultBean
    public GraphiteConfig configure(Config config) {
        final Map<String, String> properties = ConfigAdapter.captureProperties(config, PREFIX);
        return ConfigAdapter.validate(properties::get);
    }

    @Produces
    @Singleton
    @DefaultBean
    @GraphiteNameMapper
    public HierarchicalNameMapper nameMapper(GraphiteConfig config) {
        return config.graphiteTagsEnabled() ? new GraphiteDimensionalNameMapper()
                : new GraphiteHierarchicalNameMapper(config.tagsAsPrefix());
    }

    @Produces
    @Singleton
    @DefaultBean
    public GraphiteMeterRegistry registry(Instance<GraphiteMeterRegistryConfigCustomizer> customizers,
            GraphiteConfig config,
            @GraphiteNameMapper HierarchicalNameMapper nameMapper,
            Clock clock) {
        GraphiteMeterRegistry graphiteMeterRegistry = new GraphiteMeterRegistry(config, clock, nameMapper);

        List<GraphiteMeterRegistryConfigCustomizer> sortedCustomizers = sortCustomizersInDescendingPriorityOrder(customizers);

        for (GraphiteMeterRegistryConfigCustomizer customizer : sortedCustomizers) {
            customizer.customize(graphiteMeterRegistry.config());
        }

        return graphiteMeterRegistry;
    }

    private List<GraphiteMeterRegistryConfigCustomizer> sortCustomizersInDescendingPriorityOrder(
            Instance<GraphiteMeterRegistryConfigCustomizer> customizers) {
        List<GraphiteMeterRegistryConfigCustomizer> sortedCustomizers = new ArrayList<>();
        for (GraphiteMeterRegistryConfigCustomizer customizer : customizers) {
            sortedCustomizers.add(customizer);
        }
        Collections.sort(sortedCustomizers);
        return sortedCustomizers;
    }
}
