package io.quarkiverse.it.micrometer.jmx;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.*;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.quarkus.runtime.ImageMode;
import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.Route.HttpMethod;
import io.quarkus.vertx.web.RouteBase;

@RouteBase(path = "/message")
public class MessageResource {

    private final MeterRegistry registry;
    private final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    public MessageResource(MeterRegistry registry) {
        this.registry = registry;
    }

    @Route(path = "ping", methods = HttpMethod.GET)
    public String message() {
        System.out.println(ImageMode.current());
        String result = ImageMode.current().toString();

        CompositeMeterRegistry compositeMeterRegistry = (CompositeMeterRegistry) registry;
        Set<MeterRegistry> subRegistries = compositeMeterRegistry.getRegistries();
        result += ";" + (subRegistries.isEmpty() ? "empty" : subRegistries.iterator().next().getClass().getName());
        return result;
    }

    @Route(path = "fail", methods = HttpMethod.GET)
    public String fail() {
        throw new IntentionalRequestFailure();
    }

    @Route(path = "item/:id", methods = HttpMethod.GET)
    public String item(@Param("id") String id) {
        return "return message with id " + id;
    }

    @Route(path = "mbeans", methods = HttpMethod.GET)
    public String metrics() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
        Set<ObjectName> mbeans = mBeanServer.queryNames(null, null);
        StringBuilder sb = new StringBuilder();
        for (ObjectName mbean : mbeans) {
            if (mbean.getCanonicalName().startsWith("metrics:name=http")) {
                sb.append(mbean.getCanonicalName()).append("\n");
            }
        }
        return sb.toString();
    }

    static class IntentionalRequestFailure extends RuntimeException {
        public IntentionalRequestFailure() {
            super("Failed on purpose", null, false, false);
        }
    }
}
