package io.bootique.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.metrics.health.HealthCheck;
import io.bootique.metrics.health.HealthCheckGroup;
import io.bootique.metrics.health.HealthCheckRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MetricsModule extends ConfigModule {

    public MetricsModule(String configPrefix) {
        super(configPrefix);
    }

    public MetricsModule() {
    }

    /**
     * Returns an instance of {@link MetricsModuleExtender} used by downstream modules to load custom extensions for
     * the MetricsModule. Should be invoked from a downstream Module's "configure" method.
     *
     * @param binder DI binder passed to the Module that invokes this method.
     * @return an instance of {@link MetricsModuleExtender} that can be used to load MetricsModule custom extensions.
     * @since 0.9
     */
    public static MetricsModuleExtender extend(Binder binder) {
        return new MetricsModuleExtender(binder);
    }

    /**
     * @param binder DI binder passed to the Module that invokes this method.
     * @return {@link MapBinder} for Healthchecks.
     * @since 0.8
     * @deprecated since 0.9 use {@link #extend(Binder)} and then call
     * {@link MetricsModuleExtender#addHealthCheck(String, Class)} or similar method.
     */
    @Deprecated
    public static MapBinder<String, HealthCheck> contributeHealthChecks(Binder binder) {
        return MapBinder.newMapBinder(binder, String.class, HealthCheck.class);
    }

    /**
     * @param binder DI binder passed to the Module that invokes this method.
     * @return {@link MapBinder} for Healthchecks.
     * @since 0.8
     * @deprecated since 0.9 use {@link #extend(Binder)} and then call
     * {@link MetricsModuleExtender#addHealthCheckGroup(Class)} or similar method.
     */
    @Deprecated
    public static Multibinder<HealthCheckGroup> contributeHealthCheckGroups(Binder binder) {
        return Multibinder.newSetBinder(binder, HealthCheckGroup.class);
    }

    @Override
    public void configure(Binder binder) {

        // eager-load the registry. Otherwise it may never start...
        binder.bind(MetricRegistry.class).toProvider(MetricRegistryProvider.class).asEagerSingleton();

        // init DI collections and maps...
        extend(binder).initAllExtensions();
    }

    @Provides
    @Singleton
    MetricRegistryFactory provideMetricRegistryFactory(ConfigurationFactory configFactory) {
        return configFactory.config(MetricRegistryFactory.class, configPrefix);
    }

    @Provides
    @Singleton
    HealthCheckRegistry provideHealthCheckRegistry(Map<String, HealthCheck> healthChecks, Set<HealthCheckGroup> groups) {
        Map<String, HealthCheck> checks = new HashMap<>(healthChecks);
        groups.forEach(g -> checks.putAll(g.getHealthChecks()));
        return new HealthCheckRegistry(checks);
    }
}
