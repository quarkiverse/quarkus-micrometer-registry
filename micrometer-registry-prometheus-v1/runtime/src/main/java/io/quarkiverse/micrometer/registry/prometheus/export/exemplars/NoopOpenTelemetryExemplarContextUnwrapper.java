package io.quarkiverse.micrometer.registry.prometheus.export.exemplars;

import java.util.function.Function;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;

import io.quarkus.micrometer.runtime.export.exemplars.OpenTelemetryContextUnwrapper;
import io.vertx.core.Context;

@Alternative()
@Priority(2000)
@Dependent
public class NoopOpenTelemetryExemplarContextUnwrapper implements OpenTelemetryContextUnwrapper {

    @Override
    public <P, R> R executeInContext(Function<P, R> methodReference, P parameter, Context requestContext) {
        return methodReference.apply(parameter);// pass through
    }
}
