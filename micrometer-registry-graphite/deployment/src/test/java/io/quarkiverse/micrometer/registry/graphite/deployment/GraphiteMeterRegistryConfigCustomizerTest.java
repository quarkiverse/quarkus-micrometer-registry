package io.quarkiverse.micrometer.registry.graphite.deployment;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.graphite.GraphiteMeterRegistry;
import io.quarkiverse.micrometer.registry.graphite.GraphiteMeterRegistryConfigCustomizer;
import io.quarkus.test.QuarkusUnitTest;

public class GraphiteMeterRegistryConfigCustomizerTest {
    private static final NamingConvention TESTING_NAMING_CONVENTION = NamingConvention.slashes;

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
    public void testCustomizerCorrect() {
        Assertions.assertNotNull(registry, "A registry should be configured");
        Set<MeterRegistry> subRegistries = ((CompositeMeterRegistry) registry).getRegistries();
        Assertions.assertEquals(1, subRegistries.size(), "There should be a sub-registry: " + subRegistries);
        MeterRegistry meterRegistry = subRegistries.iterator().next();
        Assertions.assertEquals(GraphiteMeterRegistry.class.getName(), meterRegistry.getClass().getName());
        Assertions.assertEquals(TESTING_NAMING_CONVENTION, meterRegistry.config().namingConvention());
    }

    @Singleton
    public static class TestCustomizer implements GraphiteMeterRegistryConfigCustomizer {
        @Override
        public void customize(MeterRegistry.Config config) {
            config.namingConvention(TESTING_NAMING_CONVENTION);
        }
    }
}
