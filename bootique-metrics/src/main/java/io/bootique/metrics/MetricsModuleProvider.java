package io.bootique.metrics;

import com.google.inject.Module;
import io.bootique.BQModule;
import io.bootique.BQModuleProvider;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public class MetricsModuleProvider implements BQModuleProvider {

    @Override
    public Module module() {
        return new MetricsModule();
    }

    /**
     * @return a single entry map with {@link MetricRegistryFactory}.
     * @since 0.9
     */
    @Override
    public Map<String, Type> configs() {
        // TODO: config prefix is hardcoded. Refactor away from ConfigModule, and make provider
        // generate config prefix, reusing it in metadata...
        return Collections.singletonMap("metrics", MetricRegistryFactory.class);
    }

    @Override
    public BQModule.Builder moduleBuilder() {
        return BQModuleProvider.super
                .moduleBuilder()
                .description("Integrates Dropwizard metrics in the application.");
    }
}
