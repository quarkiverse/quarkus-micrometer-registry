package io.quarkiverse.micrometer.registry.cloudwatch2.deployment;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.test.QuarkusExtensionTest;

/**
 * The CloudWatch namespace is required: the application must not start when the
 * CloudWatch MeterRegistry is enabled without one.
 */
public class CloudWatchMissingNamespaceTest {
    static final String NAMESPACE = "quarkus.micrometer.export.cloudwatch.namespace";

    @RegisterExtension
    static final QuarkusExtensionTest config = new QuarkusExtensionTest()
            .overrideConfigKey("quarkus.micrometer.binder-enabled-default", "false")
            .overrideConfigKey("quarkus.micrometer.export.cloudwatch.enabled", "true")
            .overrideConfigKey("quarkus.micrometer.export.cloudwatch.region", "us-east-1")
            .overrideConfigKey("quarkus.micrometer.registry-enabled-default", "false")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class))
            .assertException(t -> {
                Throwable cause = rootCause(t);
                Assertions.assertEquals(ConfigurationException.class.getName(), cause.getClass().getName(),
                        "Unexpected exception: " + cause);
                Assertions.assertTrue(cause.getMessage().contains(NAMESPACE),
                        "The error should mention " + NAMESPACE + ": " + cause.getMessage());
            });

    @Test
    public void testStartupFails() {
        Assertions.fail("Runtime should not have initialized with missing " + NAMESPACE);
    }

    static Throwable rootCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }
}
