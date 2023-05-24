package io.quarkiverse.it.micrometer.jmx;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;

/**
 * Test functioning prometheus endpoint.
 * Use test execution order to ensure one http server request measurement
 * is present when the endpoint is scraped.
 */
@QuarkusIntegrationTest
class JmxMetricsRegistryIT extends JmxMetricsRegistryTest {
}
