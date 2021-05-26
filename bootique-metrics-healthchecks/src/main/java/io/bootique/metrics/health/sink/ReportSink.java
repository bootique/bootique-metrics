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
package io.bootique.metrics.health.sink;

/**
 * An abstraction for character output collector, normally used to print health check reports. The report is only
 * guaranteed to become externally visible after the {@link #close()} method is called.
 *
 * <p>Somewhat similar to
 * Java {@link Appendable} or {@link java.io.Writer}, only Exception-free and targeted to the health checking use
 * case. E.g. an implementing {@link FileReportSink} would override health check files atomically.</p>
 */
public interface ReportSink extends AutoCloseable {

    ReportSink append(String string);

    default ReportSink appendln(String string) {
        return append(string).append(System.lineSeparator());
    }

    @Override
    default void close() {
        // do nothing ; remove exceptions from super signature
    }
}
