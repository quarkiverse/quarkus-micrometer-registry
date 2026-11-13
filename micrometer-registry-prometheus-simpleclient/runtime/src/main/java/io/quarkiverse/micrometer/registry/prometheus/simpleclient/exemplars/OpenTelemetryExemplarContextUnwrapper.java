package io.quarkiverse.micrometer.registry.prometheus.simpleclient.exemplars;

import java.util.function.Function;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;
import jakarta.interceptor.Interceptor;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.quarkus.micrometer.runtime.export.exemplars.OpenTelemetryContextUnwrapper;
import io.quarkus.opentelemetry.runtime.QuarkusContextStorage;

/**
 * Takes precedence over the no-op unwrapper that Quarkus core always registers
 * when its own (new client) Prometheus registry is absent.
 */
@Dependent
@Alternative
@Priority(Interceptor.Priority.APPLICATION + 100)
public class OpenTelemetryExemplarContextUnwrapper implements OpenTelemetryContextUnwrapper {

    @Override
    public <P, R> R executeInContext(Function<P, R> methodReference, P parameter, io.vertx.core.Context requestContext) {
        if (requestContext == null) {
            return methodReference.apply(parameter);
        }

        Context newContext = QuarkusContextStorage.getOtelContext(requestContext);

        if (newContext == null) {
            return methodReference.apply(parameter);
        }

        io.opentelemetry.context.Context oldContext = QuarkusContextStorage.INSTANCE.current();
        try (Scope scope = QuarkusContextStorage.INSTANCE.attach(newContext)) {
            return methodReference.apply(parameter);
        }
    }
}
