package io.quarkiverse.it.micrometer.native_mode;

import static io.restassured.RestAssured.given;

import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.NativeImageTest;
import io.restassured.response.Response;

@NativeImageTest
class NativeMeterRegistriesIT extends NativeMeterRegistriesTest {

    @Test
    @Override
    void testRegistryInjection() {
        // We expect all 6 registries on the classpath to be initialized
        Response response = given()
                .when().get("/message/ping");

        Assertions.assertEquals(200, response.statusCode());

        List<Object> registries = response.jsonPath().getList("$");
        MatcherAssert.assertThat(registries, Matchers.containsInAnyOrder(
                "io.micrometer.datadog.DatadogMeterRegistry",
                "io.micrometer.graphite.GraphiteMeterRegistry",
                "io.micrometer.stackdriver.StackdriverMeterRegistry"));

        MatcherAssert.assertThat(registries, Matchers.not(Matchers.containsInAnyOrder(
                "io.micrometer.azuremonitor.AzureMonitorMeterRegistry",
                "io.micrometer.influx.InfluxMeterRegistry",
                "io.micrometer.jmx.JmxMeterRegistry",
                "io.micrometer.newrelic.NewRelicMeterRegistry",
                "io.micrometer.signalfx.SignalFxMeterRegistry",
                "io.micrometer.statsd.StatsdMeterRegistry")));
    }
}
