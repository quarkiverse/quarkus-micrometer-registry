package io.quarkiverse.micrometer.registry.newrelic;

import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.Config;
import org.jboss.logging.Logger;

import io.micrometer.core.instrument.Clock;
import io.micrometer.newrelic.NewRelicConfig;
import io.micrometer.newrelic.NewRelicMeterRegistry;
import io.micrometer.newrelic.NewRelicNamingConvention;
import io.quarkus.arc.DefaultBean;
import io.quarkus.micrometer.runtime.export.ConfigAdapter;

@Singleton
public class NewRelicMeterRegistryProvider {
    private static final Logger log = Logger.getLogger(NewRelicMeterRegistryProvider.class);

    static final String PREFIX = "quarkus.micrometer.export.new-relic.";
    static final String PUBLISH = "new-relic.publish";
    static final String ENABLED = "new-relic.enabled";

    @Produces
    @Singleton
    @DefaultBean
    public NewRelicConfig configure(Config config) {
        final Map<String, String> properties = ConfigAdapter.captureProperties(config, PREFIX);

        // Special check: if publish is set, override the value of enabled
        // Specifically, The new relic registry must be enabled for this
        // Provider to even be present. If this instance (at runtime) wants
        // to prevent metrics from being published, then it would set
        // quarkus.micrometer.export.new-relic.publish=false
        if (properties.containsKey(PUBLISH)) {
            properties.put(ENABLED, properties.get(PUBLISH));
        }

        return ConfigAdapter.validate(new NewRelicConfig() {
            @Override
            public String get(String key) {
                return properties.get(key);
            }
        });
    }

    @Produces
    @DefaultBean
    public NewRelicNamingConvention namingConvention() {
        return new NewRelicNamingConvention();
    }

    @Produces
    @Singleton
    public NewRelicMeterRegistry registry(NewRelicConfig config, Clock clock) {
        return NewRelicMeterRegistry.builder(config)
                .clock(clock)
                .build();
    }
}
