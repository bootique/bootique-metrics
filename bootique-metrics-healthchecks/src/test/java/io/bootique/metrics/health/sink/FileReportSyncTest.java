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

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class FileReportSyncTest {


    private static final File REPORTS_DIR = new File("target/health-reports/");
    private static final AtomicInteger COUNTER = new AtomicInteger();

    @BeforeClass
    public static void beforeClass() throws IOException {

        if (REPORTS_DIR.exists()) {
            Files.walk(REPORTS_DIR.toPath(), 10)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    private static void assertReport(File report, String... expectedLines) {
        assertEquals("Temp file was not deleted", 1, report.getParentFile().listFiles().length);

        String[] lines;
        try {
            lines = Files.lines(report.toPath()).toArray(String[]::new);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }

        assertArrayEquals(expectedLines, lines);
    }

    private File newReportDir() {

        File dir = new File(REPORTS_DIR, String.valueOf(COUNTER.getAndIncrement()));
        assertFalse(dir.exists());

        dir.mkdirs();
        assertTrue(dir.exists());
        assertTrue(dir.isDirectory());

        return dir;
    }

    @Test
    public void testWrite() {

        File dir = newReportDir();
        File report = new File(dir, "hc.txt");

        FileReportSink sink1 = new FileReportSink(report);
        sink1.append("x").appendln("y").append("z").close();
        assertReport(report, "xy", "z");

        FileReportSink sink2 = new FileReportSink(report);
        sink2.append("1").appendln("2").append("3").close();
        assertReport(report, "12", "3");
    }
}
