package io.quarkiverse.micrometer.registry.prometheus.export;

import java.util.function.Consumer;

import io.quarkiverse.micrometer.registry.prometheus.export.exemplars.NoopOpenTelemetryExemplarContextUnwrapper;
import io.quarkiverse.micrometer.registry.prometheus.export.exemplars.OpenTelemetryExemplarContextUnwrapper;
import io.quarkiverse.micrometer.registry.prometheus.export.handler.PrometheusHandler;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.runtime.annotations.RuntimeInit;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class PrometheusRecorder {

    PrometheusHandler handler;

    public PrometheusHandler getHandler() {
        if (handler == null) {
            handler = new PrometheusHandler();
        }

        return handler;
    }

    public Consumer<Route> route() {
        return new Consumer<Route>() {
            @Override
            public void accept(Route route) {
                route.order(1).produces("text/plain");
                route.order(2).produces("application/openmetrics-text; version=1.0.0; charset=utf-8");
            }
        };
    }

    public Consumer<Route> fallbackRoute() {
        return new Consumer<Route>() {
            @Override
            public void accept(Route route) {
                route.order(4);
            }
        };
    }

    public Handler<RoutingContext> getFallbackHandler() {
        return new Handler<RoutingContext>() {
            @Override
            public void handle(RoutingContext routingContext) {
                routingContext.response()
                        .setStatusCode(406)
                        .setStatusMessage(
                                "Micrometer prometheus endpoint does not support "
                                        + routingContext.request().getHeader(HttpHeaders.ACCEPT))
                        .end();
            }
        };
    }

    @RuntimeInit
    public RuntimeValue<?> createOpenTelemetryExemplarContextUnwrapper() {
        return new RuntimeValue<>(new OpenTelemetryExemplarContextUnwrapper());
    }

    @RuntimeInit
    public RuntimeValue<?> createNoopOpenTelemetryExemplarContextUnwrapper() {
        return new RuntimeValue<>(new NoopOpenTelemetryExemplarContextUnwrapper());
    }
}
