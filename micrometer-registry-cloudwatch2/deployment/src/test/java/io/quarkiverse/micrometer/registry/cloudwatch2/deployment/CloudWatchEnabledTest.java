package io.quarkiverse.micrometer.registry.cloudwatch2.deployment;

import java.util.Set;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.quarkus.test.QuarkusExtensionTest;

public class CloudWatchEnabledTest {
    static final String REGISTRY_CLASS_NAME = "io.micrometer.cloudwatch2.CloudWatchMeterRegistry";

    @RegisterExtension
    static final QuarkusExtensionTest config = new QuarkusExtensionTest()
            .overrideConfigKey("quarkus.micrometer.binder-enabled-default", "false")
            .overrideConfigKey("quarkus.micrometer.export.cloudwatch.enabled", "true")
            .overrideConfigKey("quarkus.micrometer.export.cloudwatch.publish", "false")
            .overrideConfigKey("quarkus.micrometer.export.cloudwatch.namespace", "dummy")
            .overrideConfigKey("quarkus.micrometer.export.cloudwatch.region", "us-east-1")
            .overrideConfigKey("quarkus.micrometer.registry-enabled-default", "false")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Inject
    MeterRegistry registry;

    @Test
    public void testMeterRegistryPresent() {
        // CloudWatch is enabled (alone, all others disabled)
        Assertions.assertNotNull(registry, "A registry should be configured");
        Set<MeterRegistry> subRegistries = ((CompositeMeterRegistry) registry).getRegistries();
        Assertions.assertEquals(1, subRegistries.size(),
                "There should be a sub-registry: " + subRegistries);
        Assertions.assertEquals(REGISTRY_CLASS_NAME, subRegistries.iterator().next().getClass().getName(),
                "Should be CloudWatchMeterRegistry");
    }
}
