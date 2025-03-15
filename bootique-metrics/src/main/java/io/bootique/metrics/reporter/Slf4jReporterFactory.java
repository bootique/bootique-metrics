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
import com.codahale.metrics.Slf4jReporter;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.shutdown.ShutdownManager;
import io.bootique.value.Duration;

import jakarta.inject.Inject;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * A {@link ReporterFactory} that produces a {@link Slf4jReporter}.
 */
@BQConfig("Configures a reporter that logs metrics via SLF4J.")
@JsonTypeName("slf4j")
public class Slf4jReporterFactory implements ReporterFactory {

    private final ShutdownManager shutdownManager;

    private Duration period;

    @Inject
    public Slf4jReporterFactory(ShutdownManager shutdownManager) {
        this.shutdownManager = shutdownManager;
    }

    @Override
    public Reporter createAndStart(MetricRegistry metricRegistry) {
        Slf4jReporter reporter = Slf4jReporter.forRegistry(metricRegistry).build();
        reporter.start(resolvePeriod().toMillis(), TimeUnit.MILLISECONDS);
        return shutdownManager.onShutdown(reporter);
    }

    java.time.Duration resolvePeriod() {
        return period != null ? period.getDuration() : java.time.Duration.of(30, ChronoUnit.SECONDS);
    }

    @BQConfigProperty("Set the amount of time between polls. Default value is 30 seconds.")
    public void setPeriod(Duration duration) {
        this.period = duration;
    }
}
