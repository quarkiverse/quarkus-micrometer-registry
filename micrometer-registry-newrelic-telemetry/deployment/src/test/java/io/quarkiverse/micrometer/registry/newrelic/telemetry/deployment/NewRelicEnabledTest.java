package io.quarkiverse.micrometer.registry.newrelic.telemetry.deployment;

import static io.quarkiverse.micrometer.registry.newrelic.telemetry.deployment.NewRelicTelemetryRegistryProcessor.REGISTRY_CLASS_NAME;

import java.util.Set;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.newrelic.telemetry.micrometer.NewRelicRegistryConfig;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.quarkus.test.QuarkusUnitTest;

public class NewRelicEnabledTest {
    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("test-logging.properties")
            .overrideConfigKey("quarkus.micrometer.binder-enabled-default", "false")
            .overrideConfigKey("quarkus.micrometer.export.newrelic.telemetry.enabled", "true")
            .overrideConfigKey("quarkus.micrometer.export.newrelic.telemetry.publish", "false")
            .overrideConfigKey("quarkus.micrometer.export.newrelic.telemetry.apiKey", "dummy")
            .overrideConfigKey("quarkus.micrometer.export.newrelic.telemetry.uri", "http://localhost")
            .overrideConfigKey("quarkus.micrometer.registry-enabled-default", "false")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Inject
    MeterRegistry registry;

    @Inject
    NewRelicRegistryConfig newRelicRegistryConfig;

    @Test
    public void testMeterRegistryPresent() {
        // New Relic is enabled (alone, all others disabled)
        Assertions.assertNotNull(registry, "A registry should be configured");
        Set<MeterRegistry> subRegistries = ((CompositeMeterRegistry) registry).getRegistries();
        Assertions.assertEquals(1, subRegistries.size(),
                "There should be a sub-registry: " + subRegistries);
        Assertions.assertEquals(REGISTRY_CLASS_NAME, subRegistries.iterator().next().getClass().getName(),
                "Should be NewRelicMeterRegistry");
    }

    @Test
    public void testConfigs() {
        Assertions.assertEquals("dummy", newRelicRegistryConfig.apiKey());
        Assertions.assertEquals("http://localhost", newRelicRegistryConfig.uri());
        Assertions.assertEquals("false", newRelicRegistryConfig.get("newrelic.telemetry.publish"));
        Assertions.assertFalse(newRelicRegistryConfig.enabled(), "Publish is disabled, therefore cannot be enabled");
    }
}
