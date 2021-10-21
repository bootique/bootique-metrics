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
package io.bootique.metrics.health.heartbeat;

import io.bootique.metrics.health.HealthCheckOutcome;
import io.bootique.metrics.health.sink.ReportSink;
import io.bootique.metrics.health.writer.ReportWriter;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class HeartbeatReporterTest {

    @Test
    public void testHealthChecksFinished() {

        ReportSink mockSink = mock(ReportSink.class);
        ReportWriter writer = mock(ReportWriter.class);

        HeartbeatReporter processor = new HeartbeatReporter(() -> mockSink, writer);

        Map<String, HealthCheckOutcome> results = new HashMap<>();
        results.put("x", HealthCheckOutcome.ok());

        processor.healthChecksFinished(results);

        verify(writer).write(results, mockSink);
    }
}
