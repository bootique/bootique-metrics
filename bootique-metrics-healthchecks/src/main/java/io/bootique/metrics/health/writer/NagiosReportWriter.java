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
import io.bootique.metrics.health.check.Threshold;
import io.bootique.metrics.health.check.ThresholdType;
import io.bootique.metrics.health.sink.ReportSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A {@link ReportWriter} that produces a report in a Nagios-like format.
 *
 * @since 1.0.RC1
 */
public class NagiosReportWriter implements ReportWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NagiosReportWriter.class);

    private Map<Class<?>, ValueConverter<?>> valueConverters;
    private ValueConverter<Object> defaultConverter;

    public NagiosReportWriter(Map<Class<?>, ValueConverter<?>> valueConverters) {
        this.valueConverters = valueConverters;
        this.defaultConverter = (v, i) -> v.toString();
    }

    @Override
    public void write(Map<String, HealthCheckOutcome> result, ReportSink out) {

        HealthCheckStatus status = mergedStatus(result);
        if (LOGGER.isInfoEnabled()) {

            int size = result.size();
            String message = size == 1 ? "health check was executed" : "health checks were executed";
            LOGGER.info("Health: {}.. {} {}.", status.name(), size, message);
        }

        out.append(status.name());

        if (!result.isEmpty()) {

            // using LinkedHM to preserve the ordering of keys provided by sorted stream..
            Map<String, HealthCheckData<?>> metrics = new LinkedHashMap<>();

            // sort for stable output ... collect metrics data in a separate buffer that will be appended at the end...
            result.keySet().stream().sorted().forEach(k -> {

                HealthCheckOutcome outcome = result.get(k);
                outcome.getData().ifPresent(d -> metrics.put(k, d));

                out.appendln("");
                writeResult(out, k, outcome);
            });

            if (!metrics.isEmpty()) {
                out.append("|");

                metrics.forEach((k, m) -> {
                    writeMetrics(out, k, m);
                    out.appendln("");
                });
            }
        }
    }

    protected void writeResult(ReportSink out, String key, HealthCheckOutcome outcome) {

        out.append(key).append(" ").append(outcome.getStatus().name());
        out.append(getMessage(outcome));
    }

    protected void writeMetrics(ReportSink out, String key, HealthCheckData<?> metric) {

        String label = printableLabel(key);
        String value = printableValue(metric.getValue(), true);

        String warn = printableThreshold(metric.getThresholds().getThreshold(ThresholdType.WARNING));
        String critical = printableThreshold(metric.getThresholds().getThreshold(ThresholdType.CRITICAL));
        String min = printableThreshold(metric.getThresholds().getThreshold(ThresholdType.MIN));
        String max = printableThreshold(metric.getThresholds().getThreshold(ThresholdType.MAX));

        // TODO: units of measurement per http://nagios-plugins.org/doc/guidelines.html#AEN200

        out.append(label).append("=").append(value)
                .append(";").append(warn)
                .append(";").append(critical)
                .append(";").append(min)
                .append(";").append(max);
    }

    private HealthCheckStatus mergedStatus(Map<String, HealthCheckOutcome> result) {
        if (result.isEmpty()) {
            return HealthCheckStatus.OK;
        }

        // HealthCheckOutcome is comparable ... the highest value is overall value
        return Collections.max(result.values()).getStatus();
    }

    protected String getMessage(HealthCheckOutcome outcome) {
        String message = outcome.getMessage();
        Throwable error = outcome.getError();

        if (message == null && error != null) {
            message = error.getMessage();
        }

        return message == null ? "" : " " + scrubMessage(message);
    }

    protected String scrubMessage(String message) {

        // shorten long messages
        if (message.length() > 200) {
            message = message.substring(0, 200);
        }

        // Replace :
        // * Line breaks with spaces, so that message stays in one line.
        // * Pipes with spaces - pipe is a separator in Nagios.
        // ... also trim leading and trailing space.
        return message
                .replace('\n', ' ')
                .replace('\r', ' ')
                .replace('|', ' ')
                .trim();
    }

    protected String printableLabel(String label) {
        // http://nagios-plugins.org/doc/guidelines.html#AEN200

        // 2. label can contain any characters except the equals sign or single quote (')
        // 3. the single quotes for the label are optional. Required if spaces are in the label
        // 4. label length is arbitrary, but ideally the first 19 characters are unique (due to a limitation in RRD).
        //    Be aware of a limitation in the amount of data that NRPE returns to Nagios
        // 5. to specify a quote character, use two single quotes

        StringBuilder printable = new StringBuilder(label.length() + 3);
        printable.append('\'');

        int len = label.length();
        for (int i = 0; i < len; i++) {
            char c = label.charAt(i);

            switch (c) {
                case '\n':
                case '\r':
                    printable.append(' ');
                    break;
                case '\'':
                    printable.append("''");
                    break;
                case '=':
                    printable.append('_');
                default:
                    printable.append(c);
            }
        }

        printable.append('\'');
        return printable.toString();
    }

    protected String printableThreshold(Threshold<?> threshold) {
        return threshold != null ? printableValue(threshold.getValue(), false) : "";
    }

    protected String printableValue(Object value, boolean includeUnit) {
        if (value == null) {
            return "";
        }

        return lookupConverter(value.getClass()).printableValue(value, includeUnit);
    }

    private ValueConverter lookupConverter(Class<?> type) {
        ValueConverter<?> converter = valueConverters.get(type);
        return converter != null ? converter : defaultConverter;
    }
}
