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

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Slf4JReportSinkTest {

    @Test
    public void append() {
        TestLogger logger = new TestLogger();
        new Slf4JReportSink(logger).append("one").appendln("two").append("three").close();
        assertEquals(asList("onetwo", "three"), logger.infos);
    }

    @Test
    public void append_LineBreaks() {
        String lineBreak = System.lineSeparator();
        TestLogger logger = new TestLogger();
        new Slf4JReportSink(logger).append(lineBreak + "one" + lineBreak + "two").appendln("three" + lineBreak).close();
        assertEquals(asList("", "one", "twothree", ""), logger.infos);
    }

    static class TestLogger implements Logger {

        List<String> infos = new ArrayList<>();

        @Override
        public String getName() {
            return "";
        }

        @Override
        public boolean isTraceEnabled() {
            return false;
        }

        @Override
        public void trace(String msg) {
        }

        @Override
        public void trace(String format, Object arg) {

        }

        @Override
        public void trace(String format, Object arg1, Object arg2) {

        }

        @Override
        public void trace(String format, Object... arguments) {

        }

        @Override
        public void trace(String msg, Throwable t) {

        }

        @Override
        public boolean isTraceEnabled(Marker marker) {
            return false;
        }

        @Override
        public void trace(Marker marker, String msg) {

        }

        @Override
        public void trace(Marker marker, String format, Object arg) {

        }

        @Override
        public void trace(Marker marker, String format, Object arg1, Object arg2) {

        }

        @Override
        public void trace(Marker marker, String format, Object... argArray) {

        }

        @Override
        public void trace(Marker marker, String msg, Throwable t) {

        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public void debug(String msg) {

        }

        @Override
        public void debug(String format, Object arg) {

        }

        @Override
        public void debug(String format, Object arg1, Object arg2) {

        }

        @Override
        public void debug(String format, Object... arguments) {

        }

        @Override
        public void debug(String msg, Throwable t) {

        }

        @Override
        public boolean isDebugEnabled(Marker marker) {
            return false;
        }

        @Override
        public void debug(Marker marker, String msg) {

        }

        @Override
        public void debug(Marker marker, String format, Object arg) {

        }

        @Override
        public void debug(Marker marker, String format, Object arg1, Object arg2) {

        }

        @Override
        public void debug(Marker marker, String format, Object... arguments) {

        }

        @Override
        public void debug(Marker marker, String msg, Throwable t) {
        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public void info(String msg) {
            infos.add(msg);
        }

        @Override
        public void info(String format, Object arg) {

        }

        @Override
        public void info(String format, Object arg1, Object arg2) {

        }

        @Override
        public void info(String format, Object... arguments) {

        }

        @Override
        public void info(String msg, Throwable t) {

        }

        @Override
        public boolean isInfoEnabled(Marker marker) {
            return false;
        }

        @Override
        public void info(Marker marker, String msg) {

        }

        @Override
        public void info(Marker marker, String format, Object arg) {

        }

        @Override
        public void info(Marker marker, String format, Object arg1, Object arg2) {

        }

        @Override
        public void info(Marker marker, String format, Object... arguments) {

        }

        @Override
        public void info(Marker marker, String msg, Throwable t) {

        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public void warn(String msg) {

        }

        @Override
        public void warn(String format, Object arg) {

        }

        @Override
        public void warn(String format, Object... arguments) {

        }

        @Override
        public void warn(String format, Object arg1, Object arg2) {

        }

        @Override
        public void warn(String msg, Throwable t) {

        }

        @Override
        public boolean isWarnEnabled(Marker marker) {
            return false;
        }

        @Override
        public void warn(Marker marker, String msg) {

        }

        @Override
        public void warn(Marker marker, String format, Object arg) {

        }

        @Override
        public void warn(Marker marker, String format, Object arg1, Object arg2) {

        }

        @Override
        public void warn(Marker marker, String format, Object... arguments) {

        }

        @Override
        public void warn(Marker marker, String msg, Throwable t) {

        }

        @Override
        public boolean isErrorEnabled() {
            return false;
        }

        @Override
        public void error(String msg) {

        }

        @Override
        public void error(String format, Object arg) {

        }

        @Override
        public void error(String format, Object arg1, Object arg2) {

        }

        @Override
        public void error(String format, Object... arguments) {

        }

        @Override
        public void error(String msg, Throwable t) {

        }

        @Override
        public boolean isErrorEnabled(Marker marker) {
            return false;
        }

        @Override
        public void error(Marker marker, String msg) {

        }

        @Override
        public void error(Marker marker, String format, Object arg) {

        }

        @Override
        public void error(Marker marker, String format, Object arg1, Object arg2) {

        }

        @Override
        public void error(Marker marker, String format, Object... arguments) {

        }

        @Override
        public void error(Marker marker, String msg, Throwable t) {

        }
    }
}
