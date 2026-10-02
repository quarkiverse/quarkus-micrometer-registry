package io.quarkiverse.micrometer.registry.prometheus.simpleclient;

import io.quarkiverse.micrometer.registry.prometheus.simpleclient.handlers.PrometheusSimpleHandler;
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.vertx.http.runtime.VertxHttpBuildTimeConfig;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * Only provides the scrape handler; routes and the fallback handler come from
 * {@code io.quarkus.micrometer.runtime.export.PrometheusRecorder}.
 */
@Recorder
public class PrometheusSimpleRecorder {

    PrometheusSimpleHandler handler;

    private final VertxHttpBuildTimeConfig vertxHttpBuildTimeConfig;

    public PrometheusSimpleRecorder(VertxHttpBuildTimeConfig vertxHttpBuildTimeConfig) {
        this.vertxHttpBuildTimeConfig = vertxHttpBuildTimeConfig;
    }

    public Handler<RoutingContext> getHandler() {
        if (handler == null) {
            handler = new PrometheusSimpleHandler(vertxHttpBuildTimeConfig.enableCompression(),
                    vertxHttpBuildTimeConfig.compressMediaTypes());
        }

        return handler;
    }
}
