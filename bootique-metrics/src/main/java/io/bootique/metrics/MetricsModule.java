package io.bootique.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.metrics.mdc.StripedTransactionIdGenerator;
import io.bootique.metrics.mdc.TransactionIdGenerator;
import io.bootique.metrics.mdc.TransactionIdMDC;

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
    MetricRegistryFactory provideMetricRegistryFactory(ConfigurationFactory configFactory) {
        return configFactory.config(MetricRegistryFactory.class, configPrefix);
    }

    @Provides
    @Singleton
    TransactionIdGenerator provideTransactionIdGenerator() {

        int cpus = Runtime.getRuntime().availableProcessors();
        if (cpus < 1) {
            cpus = 1;
        }
        else if(cpus > 4) {
            cpus = 4;
        }

        return new StripedTransactionIdGenerator(cpus);
    }

    @Provides
    @Singleton
    TransactionIdMDC provideTransactionIdMDC() {
        return new TransactionIdMDC();
    }
}
