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
package io.bootique.metrics.health.writer;

import io.bootique.metrics.health.HealthCheckOutcome;
import io.bootique.metrics.health.sink.ReportSink;

import java.util.Map;

/**
 * Writes formatted health check report to the provided "sink". Report format is defined by the implementation.
 *
 * @since 1.0.RC1
 */
public interface ReportWriter {

    void write(Map<String, HealthCheckOutcome> result, ReportSink out);
}
