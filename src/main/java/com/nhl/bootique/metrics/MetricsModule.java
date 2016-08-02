package com.nhl.bootique.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.nhl.bootique.ConfigModule;
import com.nhl.bootique.config.ConfigurationFactory;

public class MetricsModule extends ConfigModule {

    public MetricsModule(String configPrefix) {
        super(configPrefix);
    }

    public MetricsModule() {
    }

    @Override
    public void configure(Binder binder) {

        // eager-load the registry. Otherwise it may never start...
        binder.bind(MetricRegistry.class).toProvider(MetricRegistryProvider.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    MetricRegistryFactory createMetricRegistryFactory(ConfigurationFactory configFactory) {
        return configFactory.config(MetricRegistryFactory.class, configPrefix);
    }

    @Provides
    @Singleton
    HealthCheckRegistry createHealthcheckRegistry() {
        return new HealthCheckRegistry();
    }
}
