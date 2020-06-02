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

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.value.Duration;
import io.bootique.value.Percent;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 1.0.RC1
 */
@BQConfig
@JsonTypeName("nagios")
public class NagiosReportWriterFactory implements ReportWriterFactory {

    private int percentPrecision;

    public NagiosReportWriterFactory() {
        percentPrecision = 4;
    }

    @Override
    public ReportWriter createReportWriter() {
        return new NagiosReportWriter(createConverters());
    }

    private Map<Class<?>, ValueConverter<?>> createConverters() {
        Map<Class<?>, ValueConverter<?>> converters = new HashMap<>();
        converters.put(Percent.class, new PercentConverter(percentPrecision));
        converters.put(Duration.class, new DurationConverter());
        return converters;
    }

    @BQConfigProperty("Defines precision of percent values in the report. The default is 4.")
    public void setPercentPrecision(int percentPrecision) {
        this.percentPrecision = percentPrecision;
    }
}
