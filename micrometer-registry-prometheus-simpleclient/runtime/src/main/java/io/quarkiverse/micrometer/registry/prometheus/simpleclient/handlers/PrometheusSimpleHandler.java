package io.quarkiverse.micrometer.registry.prometheus.simpleclient.handlers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;

import org.jboss.logging.Logger;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import io.quarkus.arc.Arc;
import io.quarkus.arc.ManagedContext;
import io.quarkus.vertx.http.runtime.HttpCompressionHandler;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * Copy of {@code io.quarkus.micrometer.runtime.export.handlers.PrometheusHandler} that scrapes the
 * simpleclient {@code io.micrometer.prometheus.PrometheusMeterRegistry} instead of the
 * {@code io.micrometer.prometheusmetrics} one, which is not on the classpath with this extension.
 */
public class PrometheusSimpleHandler implements Handler<RoutingContext> {
    private static final Logger log = Logger.getLogger(PrometheusSimpleHandler.class);
    private final boolean enableCompression;
    private final Set<String> compressMediaTypes;

    private PrometheusMeterRegistry registry;

    private boolean setup = false;

    public PrometheusSimpleHandler(boolean enableCompression, Optional<List<String>> compressMediaTypes) {
        this.enableCompression = enableCompression;
        this.compressMediaTypes = determineCompressMediaTypes(compressMediaTypes);
    }

    private Set<String> determineCompressMediaTypes(Optional<List<String>> maybeCompressMediaTypes) {
        if (maybeCompressMediaTypes.isPresent()) {
            List<String> compressMediaTypes = maybeCompressMediaTypes.get();
            if (compressMediaTypes.contains("text/plain")) {
                return Set.of("text/plain", "application/openmetrics-text");
            }
        }
        return Collections.emptySet();
    }

    @Override
    public void handle(RoutingContext routingContext) {
        if (!setup) {
            setup();
        }

        if (enableCompression) {
            routingContext.addHeadersEndHandler(new Handler<>() {

                @Override
                public void handle(Void result) {
                    HttpCompressionHandler.compressIfNeeded(routingContext, compressMediaTypes);
                }
            });
        }

        HttpServerResponse response = routingContext.response();
        if (registry == null) {
            response.setStatusCode(500)
                    .setStatusMessage("Unable to resolve Prometheus registry instance")
                    .end();
        } else {
            ManagedContext requestContext = Arc.container().requestContext();
            var acceptHeader = chooseContentType(routingContext.request().getHeader("Accept"));
            if (requestContext.isActive()) {
                doHandle(response, acceptHeader);
            } else {
                requestContext.activate();
                try {
                    doHandle(response, acceptHeader);
                } finally {
                    requestContext.terminate();
                }
            }
        }
    }

    private String chooseContentType(String acceptHeader) {
        if (acceptHeader == null) {
            return TextFormat.CONTENT_TYPE_OPENMETRICS_100;
        }
        if (acceptHeader.contains("text/plain") || acceptHeader.contains("text/html")) {
            return TextFormat.CONTENT_TYPE_004;
        }
        return TextFormat.CONTENT_TYPE_OPENMETRICS_100;
    }

    private void doHandle(HttpServerResponse response, String acceptHeader) {
        response.putHeader("Content-Type", acceptHeader)
                .end(Buffer.buffer(registry.scrape(acceptHeader)));
    }

    private void setup() {
        Instance<PrometheusMeterRegistry> registries = CDI.current().select(PrometheusMeterRegistry.class,
                Default.Literal.INSTANCE);

        if (registries.isUnsatisfied()) {
            registry = null;
        } else if (registries.isAmbiguous()) {
            registry = registries.iterator().next();
            log.warnf("Multiple prometheus registries present. Using %s with the built-in scrape endpoint",
                    registry);
        } else {
            registry = registries.get();
        }

        setup = true;
    }
}
