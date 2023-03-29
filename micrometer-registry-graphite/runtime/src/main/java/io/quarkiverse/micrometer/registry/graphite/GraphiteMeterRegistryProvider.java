package io.quarkiverse.micrometer.registry.graphite;

import java.util.Map;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.graphite.GraphiteConfig;
import io.micrometer.graphite.GraphiteDimensionalNameMapper;
import io.micrometer.graphite.GraphiteHierarchicalNameMapper;
import io.quarkiverse.micrometer.registry.graphite.GraphiteConfig.GraphiteRuntimeConfig;
import io.quarkus.arc.DefaultBean;
import io.quarkus.micrometer.runtime.export.ConfigAdapter;

/**
 * @see ConditionalRegistryProducer
 */
@Singleton
public class GraphiteMeterRegistryProvider {
    static final String DEFAULT_REGISTRY = "quarkus.micrometer.export.graphite.default-registry";
    static final String PREFIX = "graphite.";

    static final String PUBLISH = "graphite.publish";
    static final String ENABLED = "graphite.enabled";

    @Produces
    @Singleton
    @DefaultBean
    public GraphiteConfig configure(GraphiteRuntimeConfig config) {
        final Map<String, String> properties = ConfigAdapter.captureProperties(config.graphite, PREFIX);

        // Special check: if publish is set, override the value of enabled
        // Specifically, the StackDriver registry must be enabled for this
        // Provider to even be present. If this instance (at runtime) wants
        // to prevent metrics from being published, then it would set
        // quarkus.micrometer.export.graphite.publish=false
        if (properties.containsKey(PUBLISH)) {
            properties.put(ENABLED, properties.get(PUBLISH));
        }

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
}
