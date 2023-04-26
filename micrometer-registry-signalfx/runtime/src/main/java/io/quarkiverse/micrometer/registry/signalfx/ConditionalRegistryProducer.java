package io.quarkiverse.micrometer.registry.signalfx;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import jakarta.interceptor.Interceptor;

import io.micrometer.core.instrument.Clock;
import io.micrometer.signalfx.SignalFxConfig;
import io.micrometer.signalfx.SignalFxMeterRegistry;

@Singleton
public class ConditionalRegistryProducer {
    /**
     * This producer is added as a bean by the Processor IFF the default registry
     * instance has been enabled.
     */
    @Produces
    @Singleton
    @Alternative
    @Priority(Interceptor.Priority.APPLICATION + 100)
    public SignalFxMeterRegistry registry(SignalFxConfig config, Clock clock) {
        return new SignalFxMeterRegistry(config, clock);
    }
}
