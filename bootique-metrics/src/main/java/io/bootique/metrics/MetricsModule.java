package io.bootique.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.metrics.healthcheck.HealthCheckRegistry;

import java.util.Map;

public class MetricsModule extends ConfigModule {

    public MetricsModule(String configPrefix) {
        super(configPrefix);
    }

    public MetricsModule() {
    }

    /**
     * @param binder DI binder passed to the Module that invokes this method.
     * @return {@link MapBinder} for Healthchecks.
     * @since 0.8
     */
    public static MapBinder<String, HealthCheck> contributeHealthchecks(Binder binder) {
        return MapBinder.newMapBinder(binder, String.class, HealthCheck.class);
    }

    @Override
    public void configure(Binder binder) {

        // eager-load the registry. Otherwise it may never start...
        binder.bind(MetricRegistry.class).toProvider(MetricRegistryProvider.class).asEagerSingleton();

        // init DI collections and maps...
        contributeHealthchecks(binder);
    }

    @Provides
    @Singleton
    MetricRegistryFactory createMetricRegistryFactory(ConfigurationFactory configFactory) {
        return configFactory.config(MetricRegistryFactory.class, configPrefix);
    }

    @Provides
    @Singleton
    HealthCheckRegistry createHealthcheckRegistry(Map<String, HealthCheck> healthChecks) {
        return new HealthCheckRegistry(healthChecks);
    }
}
