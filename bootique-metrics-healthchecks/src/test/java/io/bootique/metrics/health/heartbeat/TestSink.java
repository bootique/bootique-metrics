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

import io.bootique.metrics.health.sink.ReportSink;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class TestSink implements ReportSink {

    private static String LAST_REPORT;

    public static void reset() {
        LAST_REPORT = null;
    }

    public static void assertNoReport() {
        assertNull(LAST_REPORT);
    }

    public static void assertReport(String... expectedReportLines) {

        assertNotNull(LAST_REPORT);

        StringBuilder expected = new StringBuilder();
        for (String s : expectedReportLines) {
            if (expected.length() > 0) {
                expected.append(System.lineSeparator());
            }

            expected.append(s);
        }

        assertEquals(expected.toString(), LAST_REPORT);
    }

    private final StringBuilder currentReport;

    public TestSink() {
        this.currentReport = new StringBuilder();
    }

    @Override
    public ReportSink append(String string) {
        Objects.requireNonNull(currentReport, "sink is closed").append(string);
        return this;
    }

    @Override
    public void close() {
        LAST_REPORT = currentReport.toString();
        currentReport.setLength(0);
    }
}
