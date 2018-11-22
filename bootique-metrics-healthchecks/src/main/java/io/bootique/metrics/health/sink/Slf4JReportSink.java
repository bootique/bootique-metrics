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

import org.slf4j.Logger;

/**
 * A {@link ReportSink} writing to an Slf4J logger.
 *
 * @since 1.0.RC1
 */
public class Slf4JReportSink implements ReportSink {

    private static final String LINE_BREAK = System.lineSeparator();
    private static int LINE_BREAK_LEN = LINE_BREAK.length();

    // accumulate lines in the buffer, flushing every now and then
    private StringBuilder buffer;
    private Logger logger;

    public Slf4JReportSink(Logger logger) {
        this.logger = logger;
    }

    @Override
    public ReportSink append(String string) {

        StringBuilder buffer = createOrInitLineBuffer();

        // call logger once per line...
        int i = string.indexOf(LINE_BREAK);
        if (i < 0) {
            buffer.append(string);
        } else {
            buffer.append(string.substring(0, i));
            flush();
            append(string.substring(i + LINE_BREAK_LEN));
        }

        return this;
    }

    @Override
    public ReportSink appendln(String string) {
        append(string);
        flush();
        return this;
    }

    @Override
    public void close() {
        flush();
    }

    private StringBuilder createOrInitLineBuffer() {
        return buffer != null ? buffer : (buffer = new StringBuilder());
    }

    private void flush() {
        if (buffer != null) {
            String line = buffer.toString();
            this.buffer = null;
            logger.info(line);
        }
    }
}
