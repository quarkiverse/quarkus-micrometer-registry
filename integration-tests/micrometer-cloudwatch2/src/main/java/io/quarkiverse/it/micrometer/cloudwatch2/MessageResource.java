package io.quarkiverse.it.micrometer.cloudwatch2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.Route.HttpMethod;
import io.quarkus.vertx.web.RouteBase;

@RouteBase(path = "/message")
public class MessageResource {

    private final MeterRegistry registry;

    public MessageResource(MeterRegistry registry) {
        this.registry = registry;
    }

    @Route(path = "ping", methods = HttpMethod.GET)
    public List<String> message() {
        CompositeMeterRegistry compositeMeterRegistry = (CompositeMeterRegistry) registry;
        Set<MeterRegistry> subRegistries = compositeMeterRegistry.getRegistries();

        List<String> names = new ArrayList<>(subRegistries.size());
        subRegistries.forEach(x -> names.add(x.getClass().getName()));
        return names;
    }

    @Route(path = "count", methods = HttpMethod.GET)
    public String count() {
        registry.counter("cloudwatch.it.counter", "env", "test").increment();
        return "ok";
    }
}
