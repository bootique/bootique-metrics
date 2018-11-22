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

import io.bootique.metrics.health.HealthCheckData;
import io.bootique.metrics.health.HealthCheckOutcome;
import io.bootique.metrics.health.HealthCheckStatus;
import io.bootique.metrics.health.check.ValueRange;
import io.bootique.metrics.health.sink.ReportSink;
import io.bootique.metrics.health.sink.WriterReportSink;
import io.bootique.value.Duration;
import io.bootique.value.Percent;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class NagiosReportWriterTest {

    @Test
    public void testWrite_Empty() {
        ReportTester tester = new ReportTester();
        new NagiosReportWriter(Collections.emptyMap()).write(Collections.emptyMap(), tester.getSink());
        tester.testReport(HealthCheckStatus.OK);
    }

    @Test
    public void testWrite() {
        Map<String, HealthCheckOutcome> results = new HashMap<>();
        results.put("m.n", HealthCheckOutcome.ok());
        results.put("c.d", HealthCheckOutcome.warning("I am warning"));
        results.put("a.b", HealthCheckOutcome.critical("I am unhealthy\n"));
        results.put("x.y", HealthCheckOutcome.unknown(new Exception("I am |exception")));

        ReportTester tester = new ReportTester();
        new NagiosReportWriter(Collections.emptyMap()).write(results, tester.getSink());

        tester.testReport(HealthCheckStatus.CRITICAL,
                "a.b CRITICAL I am unhealthy",
                "c.d WARNING I am warning",
                "m.n OK",
                "x.y UNKNOWN I am  exception");
    }

    @Test
    public void testWrite_PerfData() {
        Map<String, HealthCheckOutcome> results = new HashMap<>();

        ValueRange<Integer> mnRange = ValueRange.builder(Integer.class).min(0).critical(8).build();
        ValueRange<Integer> cdRange = ValueRange.builder(Integer.class).min(-1).warning(0).critical(999).max(10000).build();
        ValueRange<Double> abRange = ValueRange.builder(Double.class).min(-1.1).warning(0.4).critical(99.).max(100.5).build();

        results.put("m.n", HealthCheckOutcome.ok().withData(new HealthCheckData<>(6, mnRange)));
        results.put("c.d", HealthCheckOutcome.warning("I am warning").withData(new HealthCheckData<>(99, cdRange)));
        results.put("a.b", HealthCheckOutcome.critical("I am unhealthy\n").withData(new HealthCheckData<>(99.5, abRange)));
        results.put("x.y", HealthCheckOutcome.unknown(new Exception("I am |exception")));

        ReportTester tester = new ReportTester();
        new NagiosReportWriter(Collections.emptyMap()).write(results, tester.getSink());

        // https://assets.nagios.com/downloads/nagioscore/docs/nagioscore/3/en/pluginapi.html
        // http://nagios-plugins.org/doc/guidelines.html#AEN200
        tester.testReport(HealthCheckStatus.CRITICAL,
                "a.b CRITICAL I am unhealthy",
                "c.d WARNING I am warning",
                "m.n OK",
                "x.y UNKNOWN I am  exception|'a.b'=99.5;0.4;99.0;-1.1;100.5",
                "'c.d'=99;0;999;-1;10000",
                "'m.n'=6;;8;0;");
    }

    @Test
    public void testWrite_PerfData_Percent_Units() {
        Map<String, HealthCheckOutcome> results = new HashMap<>();

        ValueRange<Percent> range = ValueRange
                .builder(Percent.class)
                .min(Percent.ZERO)
                .warning(new Percent(0.41))
                .critical(new Percent(0.6))
                .max(Percent.HUNDRED)
                .build();

        results.put("m.n", HealthCheckOutcome.critical().withData(new HealthCheckData<>(new Percent(0.61), range)));

        ReportTester tester = new ReportTester();
        new NagiosReportWriter(Collections.singletonMap(Percent.class, new PercentConverter(4)))
                .write(results, tester.getSink());

        // http://nagios-plugins.org/doc/guidelines.html#AEN200
        tester.testReport(HealthCheckStatus.CRITICAL, "m.n CRITICAL|'m.n'=61%;41;60;0;100");
    }

    @Test
    public void testWrite_PerfData_Duration_Units() {
        Map<String, HealthCheckOutcome> results = new HashMap<>();

        ValueRange<Duration> range = ValueRange
                .builder(Duration.class)
                .min(Duration.ZERO)
                .warning(new Duration(100))
                .critical(new Duration(200))
                .max(new Duration(1000))
                .build();

        results.put("m.n", HealthCheckOutcome.warning().withData(new HealthCheckData<>(new Duration(105), range)));

        ReportTester tester = new ReportTester();
        new NagiosReportWriter(Collections.singletonMap(Duration.class, new DurationConverter()))
                .write(results, tester.getSink());

        // http://nagios-plugins.org/doc/guidelines.html#AEN200
        tester.testReport(HealthCheckStatus.WARNING, "m.n WARNING|'m.n'=105ms;100;200;0;1000");
    }

    @Test
    public void testScrubMessage() {
        NagiosReportWriter writer = new NagiosReportWriter(Collections.emptyMap());
        assertEquals("", writer.scrubMessage(""));
        assertEquals("abc def", writer.scrubMessage(" abc def "));
        assertEquals("abc  def", writer.scrubMessage("\rabc\n def "));
        assertEquals("'|' is a separator in Nagios. Must replace.", "a b", writer.scrubMessage("a|b"));
    }

    static class ReportTester {

        StringWriter out = new StringWriter();

        public ReportSink getSink() {
            return new WriterReportSink(out);
        }

        public void testReport(HealthCheckStatus expectedStatus, String... checkLines) {

            int expectedLines = checkLines.length + 1;

            String[] lines = out.toString().split(System.lineSeparator());
            assertEquals("Unexpected number of lines in the report", expectedLines, lines.length);

            // first line is an overall status
            if (expectedLines > 0) {
                assertEquals(expectedStatus.name(), lines[0]);

                for (int i = 0; i < checkLines.length; i++) {
                    assertEquals(checkLines[i], lines[i + 1]);
                }
            }
        }
    }
}
