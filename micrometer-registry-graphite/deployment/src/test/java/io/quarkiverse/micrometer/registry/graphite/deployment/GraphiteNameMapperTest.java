package io.quarkiverse.micrometer.registry.graphite.deployment;

import java.lang.reflect.Field;
import java.util.Set;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.dropwizard.DropwizardMeterRegistry;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.graphite.GraphiteMeterRegistry;
import io.quarkiverse.micrometer.registry.graphite.GraphiteNameMapper;
import io.quarkus.arc.Priority;
import io.quarkus.test.QuarkusUnitTest;

public class GraphiteNameMapperTest {
    static final String REGISTRY_CLASS_NAME = "io.micrometer.graphite.GraphiteMeterRegistry";

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("test-logging.properties")
            .overrideConfigKey("quarkus.micrometer.binder-enabled-default", "false")
            .overrideConfigKey("quarkus.micrometer.export.graphite.enabled", "true")
            .overrideConfigKey("quarkus.micrometer.registry-enabled-default", "false")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Inject
    MeterRegistry registry;

    @Test
    public void testNameMapperCorrect() throws NoSuchFieldException, IllegalAccessException {
        Assertions.assertNotNull(registry, "A registry should be configured");
        Set<MeterRegistry> subRegistries = ((CompositeMeterRegistry) registry).getRegistries();
        Assertions.assertEquals(1, subRegistries.size(), "There should be a sub-registry: " + subRegistries);

        MeterRegistry meterRegistry = subRegistries.iterator().next();
        Assertions.assertEquals(GraphiteMeterRegistry.class.getName(), meterRegistry.getClass().getName());

        Field nameMapperField = DropwizardMeterRegistry.class.getDeclaredField("nameMapper");
        nameMapperField.setAccessible(true);
        HierarchicalNameMapper hierarchicalNameMapper = (HierarchicalNameMapper) nameMapperField.get(meterRegistry);

        Assertions.assertEquals(HierarchicalNameMapper.DEFAULT, hierarchicalNameMapper);
    }

    @Singleton
    public static class TestCustomizer {
        @Produces
        @Singleton
        @GraphiteNameMapper
        @Alternative
        @Priority(1)
        public HierarchicalNameMapper hierarchicalNameMapper() {
            return HierarchicalNameMapper.DEFAULT;
        }
    }
}
