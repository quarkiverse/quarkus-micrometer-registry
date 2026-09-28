package io.quarkiverse.it.micrometer.cloudwatch2;

import static io.restassured.RestAssured.when;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.Datapoint;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.Metric;
import software.amazon.awssdk.services.cloudwatch.model.Statistic;

@QuarkusTest
@QuarkusTestResource(FlociResource.class)
class CloudWatchMetricsRegistryTest {

    static final String NAMESPACE = "quarkus-micrometer-it";
    // The CloudWatch registry publishes counters with a ".count" suffix
    static final String COUNTER = "cloudwatch.it.counter.count";

    @Test
    void testRegistryInjection() {
        List<String> registries = when().get("/message/ping")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("$", String.class);

        MatcherAssert.assertThat(registries,
                Matchers.contains("io.micrometer.cloudwatch2.CloudWatchMeterRegistry"));
    }

    @Test
    void testMetricsArePublished() {
        for (int i = 0; i < 3; i++) {
            when().get("/message/count").then().statusCode(200);
        }

        try (CloudWatchClient client = FlociResource.cloudWatchClient()) {
            // The counter is published with its tags as CloudWatch dimensions
            Metric metric = await().atMost(Duration.ofSeconds(30))
                    .until(() -> findCounter(client), m -> m != null);
            MatcherAssert.assertThat(metric.dimensions(),
                    Matchers.hasItem(Dimension.builder().name("env").value("test").build()));

            // The 3 increments can be spread over several steps, so wait until they are all published
            await().atMost(Duration.ofSeconds(30))
                    .until(() -> counterSum(client, metric.dimensions()), sum -> sum >= 3.0);
        }
    }

    static Metric findCounter(CloudWatchClient client) {
        return client.listMetrics(r -> r.namespace(NAMESPACE).metricName(COUNTER)).metrics()
                .stream().findFirst().orElse(null);
    }

    static double counterSum(CloudWatchClient client, List<Dimension> dimensions) {
        Instant now = Instant.now();
        return client.getMetricStatistics(r -> r.namespace(NAMESPACE)
                .metricName(COUNTER)
                .dimensions(dimensions)
                .startTime(now.minus(Duration.ofMinutes(10)))
                .endTime(now.plus(Duration.ofMinutes(1)))
                .period(60)
                .statistics(Statistic.SUM))
                .datapoints().stream()
                .mapToDouble(Datapoint::sum)
                .sum();
    }
}
