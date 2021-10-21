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
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class Slf4JReportSinkTest {

    @Test
    public void testAppend() {
        Logger logger = mock(Logger.class);

        Slf4JReportSink sink = new Slf4JReportSink(logger);

        ArgumentCaptor<String> args = ArgumentCaptor.forClass(String.class);
        sink.append("one").appendln("two").append("three").close();
        verify(logger, times(2)).info(args.capture());

        assertEquals(asList("onetwo", "three"), args.getAllValues());
    }

    @Test
    public void testAppend_LineBreaks() {

        String lineBreak = System.lineSeparator();
        Logger logger = mock(Logger.class);

        Slf4JReportSink sink = new Slf4JReportSink(logger);

        ArgumentCaptor<String> args = ArgumentCaptor.forClass(String.class);
        sink.append(lineBreak + "one" + lineBreak + "two").appendln("three" + lineBreak).close();
        verify(logger, times(4)).info(args.capture());

        assertEquals(asList("", "one", "twothree", ""), args.getAllValues());
    }
}
