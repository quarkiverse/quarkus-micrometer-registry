package io.quarkiverse.micrometer.registry.graphite;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.graphite.GraphiteMeterRegistry;

/**
 * Meant to be implemented by a CDI bean that provides arbitrary customization for the default {@link GraphiteMeterRegistry}
 * config.
 * <p>
 * All implementations (that are registered as CDI beans) are taken into account when producing the default
 * {@link GraphiteMeterRegistry}.
 * <p>
 * See also {@link GraphiteMeterRegistryProvider#registry}.
 */
public interface GraphiteMeterRegistryConfigCustomizer extends Comparable<GraphiteMeterRegistryConfigCustomizer> {
    int DEFAULT_PRIORITY = 0;

    void customize(MeterRegistry.Config config);

    /**
     * Defines the priority that the customizers are applied.
     * A lower integer value means that the customizer will be applied after a customizer with a higher priority
     */
    default int priority() {
        return DEFAULT_PRIORITY;
    }

    default int compareTo(GraphiteMeterRegistryConfigCustomizer o) {
        return Integer.compare(o.priority(), priority());
    }
}
