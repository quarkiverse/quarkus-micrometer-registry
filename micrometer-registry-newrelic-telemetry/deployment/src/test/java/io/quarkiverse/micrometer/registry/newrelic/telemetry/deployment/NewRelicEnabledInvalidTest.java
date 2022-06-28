package io.quarkiverse.micrometer.registry.newrelic.telemetry.deployment;

import java.util.Arrays;
import java.util.List;
import java.util.logging.LogRecord;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.micrometer.core.instrument.config.validate.ValidationException;
import io.quarkus.test.QuarkusUnitTest;

public class NewRelicEnabledInvalidTest {
    final static String TESTED_ATTRIBUTE = "quarkus.micrometer.export.newrelic.telemetry.api-key";

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("test-logging.properties")
            .overrideConfigKey("quarkus.micrometer.binder-enabled-default", "false")
            .overrideConfigKey("quarkus.micrometer.export.newrelic.telemetry.enabled", "true")
            .overrideConfigKey("quarkus.micrometer.registry-enabled-default", "false")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class))
            .setLogRecordPredicate(r -> "io.quarkus.micrometer.runtime.export.ConfigAdapter".equals(r.getLoggerName()))
            .assertLogRecords(r -> assertMessage(TESTED_ATTRIBUTE, r))
            .assertException(t -> Assertions.assertEquals(ValidationException.class.getName(), t.getClass().getName(),
                    "Unexpected exception in test: " + stackToString(t)));

    @Test
    public void testMeterRegistryPresent() {
        Assertions.fail("Runtime should not have initialized with missing " + TESTED_ATTRIBUTE);
    }

    static void assertMessage(String attribute, List<LogRecord> records) {
        // look through log records and make sure there is a message about the specific attribute
        long i = records.stream().filter(x -> Arrays.stream(x.getParameters()).anyMatch(y -> y.equals(attribute)))
                .count();
        Assertions.assertEquals(1, i);
    }

    static String stackToString(Throwable t) {
        StringBuilder sb = new StringBuilder().append("\n");
        while (t.getCause() != null) {
            t = t.getCause();
        }
        sb.append(t.getClass()).append(": ").append(t.getMessage()).append("\n");
        Arrays.asList(t.getStackTrace()).forEach(x -> sb.append("\t").append(x.toString()).append("\n"));
        return sb.toString();
    }
}
