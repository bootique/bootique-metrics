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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * A {@link ReportSink} writing data to a temp file. Once closed, atomically replaces the target file with the new file.
 *
 * @since 1.0.RC1
 */
public class FileReportSink implements ReportSink {

    private File targetFile;
    private File tempFile;
    private ReportSink tempFileSink;

    public FileReportSink(File targetFile) {
        this.targetFile = targetFile;

        // write to temp file in the same directory... will replace the target file atomically on close...
        this.tempFile = createTempFile(targetFile);
        this.tempFileSink = new WriterReportSink(createWriter(tempFile));
    }

    private static Writer createWriter(File file) {
        try {
            return new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            throw new RuntimeException("Error opening temp file " + file, e);
        }
    }

    private static File createTempFile(File targetFile) {
        try {
            return File.createTempFile(targetFile.getName(), ".tmp", targetFile.getParentFile());
        } catch (IOException e) {
            throw new RuntimeException("Error creating temp file for file " + targetFile, e);
        }
    }

    @Override
    public ReportSink append(String string) {
        tempFileSink.append(string);
        return this;
    }

    @Override
    public ReportSink appendln(String string) {
        tempFileSink.appendln(string);
        return this;
    }

    @Override
    public void close() {
        tempFileSink.close();

        try {
            Files.move(tempFile.toPath(),
                    targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new RuntimeException("Error moving temp file to the final location", e);
        }
    }
}
