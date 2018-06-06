/**
 *    Licensed to the ObjectStyle LLC under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ObjectStyle LLC licenses
 *  this file to you under the Apache License, Version 2.0 (the
 *  “License”); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.bootique.metrics;

import com.codahale.metrics.MetricRegistry;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.metrics.reporter.ReporterFactory;
import io.bootique.shutdown.ShutdownManager;

import java.util.List;

@BQConfig("Configures MetricsRegistry")
public class MetricRegistryFactory {

    private List<ReporterFactory> reporters;

    public MetricRegistry createMetricsRegistry(ShutdownManager shutdownManager) {

        MetricRegistry registry = new MetricRegistry();

        if (reporters != null) {
            reporters.forEach(reporterFactory -> reporterFactory.installReporter(registry, shutdownManager));
        }

        return registry;
    }

    List<ReporterFactory> getReporters() {
        return reporters;
    }

    @BQConfigProperty("A List of reporter factories.")
    public void setReporters(List<ReporterFactory> reporters) {
        this.reporters = reporters;
    }
}
