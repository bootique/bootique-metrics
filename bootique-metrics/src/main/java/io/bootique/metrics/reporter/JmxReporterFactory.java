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

package io.bootique.metrics.reporter;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Reporter;
import com.codahale.metrics.jmx.JmxReporter;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.shutdown.ShutdownManager;

import jakarta.inject.Inject;

/**
 * A {@link ReporterFactory} that produces a {@link JmxReporter}.
 */
@BQConfig("Configures JMX reporter. Metrics MBeans will have 'bq.metrics' prefix.")
@JsonTypeName("jmx")
public class JmxReporterFactory implements ReporterFactory {

    private final ShutdownManager shutdownManager;

    @Inject
    public JmxReporterFactory(ShutdownManager shutdownManager) {
        this.shutdownManager = shutdownManager;
    }

    @Override
    public Reporter createAndStart(MetricRegistry metricRegistry) {
        JmxReporter reporter = JmxReporter.forRegistry(metricRegistry).inDomain("bq.metrics").build();
        reporter.start();
        return shutdownManager.onShutdown(reporter);
    }
}
