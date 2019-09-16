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

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @since 1.0.RC1
 */
@BQConfig
@JsonTypeName("file")
public class FileReportSinkFactory implements ReportSinkFactory {

    private File file;
    private String permissions;

    private static void setupReportDirectory(File file) {
        File parentDir = file.getParentFile();

        if (parentDir != null) {
            parentDir.mkdirs();

            if (!parentDir.isDirectory()) {
                throw new IllegalStateException("Can't create parent directory for the heartbeat report: " + parentDir.getAbsolutePath());
            }
        }
    }

    @Override
    public Supplier<ReportSink> createReportSyncSupplier() {
        final File file = getFile();
        return () -> new FileReportSink(file, permissions);
    }

    private File getFile() {
        Objects.requireNonNull(file, "'healthreport.file' (heartbeat report file) must be configured.");
        setupReportDirectory(file);
        return file;
    }

    @BQConfigProperty
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @param permissions Nullable string representation of permissions to set
     * @see java.nio.file.Files#createTempFile(Path, String, String, FileAttribute[])
     * @see java.nio.file.attribute.PosixFilePermissions#fromString(String)
     * @since 1.1
     */
    @BQConfigProperty("Desired Unix permissions for the sink file. " +
            "The format is a 9 character string with 3 sets of 'r', 'w', 'x', '-' symbols. E.g. 'rw-r--r--'." +
            "Optional. If undefined, permissions will be controlled by 'umask' of the environment.")
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
