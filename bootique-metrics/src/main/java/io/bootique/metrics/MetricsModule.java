/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.metrics;

import com.codahale.metrics.MetricRegistry;
import io.bootique.BQModule;
import io.bootique.ModuleCrate;
import io.bootique.config.ConfigurationFactory;
import io.bootique.di.Binder;
import io.bootique.di.Provides;
import io.bootique.metrics.mdc.StripedTransactionIdGenerator;
import io.bootique.metrics.mdc.TransactionIdGenerator;
import jakarta.inject.Singleton;

public class MetricsModule implements BQModule {

    private static final String CONFIG_PREFIX = "metrics";

    @Override
    public ModuleCrate crate() {
        return ModuleCrate.of(this)
                .description("Integrates Dropwizard metrics.")
                .config(CONFIG_PREFIX, MetricRegistryFactory.class)
                .build();
    }

    @Override
    public void configure(Binder binder) {
        // eager-load the registry. Otherwise, it may never start...
        binder.bind(MetricRegistry.class).toProvider(MetricRegistryProvider.class).initOnStartup();
    }

    @Provides
    @Singleton
    MetricRegistryFactory provideMetricRegistryFactory(ConfigurationFactory configFactory) {
        return configFactory.config(MetricRegistryFactory.class, CONFIG_PREFIX);
    }

    @Provides
    @Singleton
    TransactionIdGenerator provideTransactionIdGenerator() {

        int cpus = Runtime.getRuntime().availableProcessors();
        if (cpus < 1) {
            cpus = 1;
        } else if (cpus > 4) {
            cpus = 4;
        }

        return new StripedTransactionIdGenerator(cpus);
    }
}
