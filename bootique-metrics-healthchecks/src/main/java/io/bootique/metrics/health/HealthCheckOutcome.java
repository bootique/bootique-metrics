/**
 *  Licensed to ObjectStyle LLC under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ObjectStyle LLC licenses
 *  this file to you under the Apache License, Version 2.0 (the
 *  “License”); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.bootique.metrics.health;

import java.util.Optional;

/**
 * @since 0.8
 */
public class HealthCheckOutcome implements Comparable<HealthCheckOutcome> {

    private static final HealthCheckOutcome OK = new HealthCheckOutcome(HealthCheckStatus.OK, null, null);

    private HealthCheckStatus status;
    private String message;
    private Throwable error;
    private Optional<HealthCheckData<?>> data;

    private HealthCheckOutcome(HealthCheckStatus status, String message, Throwable error) {
        this.status = status;
        this.message = message;
        this.error = error;
        this.data = Optional.empty();
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a healthy state of the app.
     * @since 0.25
     */
    public static HealthCheckOutcome ok() {
        return OK;
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a healthy state of the app.
     * @since 0.25
     */
    public static HealthCheckOutcome ok(String message) {
        return new HealthCheckOutcome(HealthCheckStatus.OK, message, null);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a "warning" state of the app, i.e. approaching critical.
     * @since 0.25
     */
    public static HealthCheckOutcome warning() {
        return warning((String) null);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a "warning" state of the app, i.e. approaching critical.
     * @since 0.25
     */
    public static HealthCheckOutcome warning(String message) {
        return new HealthCheckOutcome(HealthCheckStatus.WARNING, message, null);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a "warning" state of the app, i.e. approaching critical.
     * @since 0.26
     */
    public static HealthCheckOutcome warning(Throwable th) {
        return new HealthCheckOutcome(HealthCheckStatus.WARNING, null, th);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a critical state of the app.
     * @since 0.25
     */
    public static HealthCheckOutcome critical() {
        return critical((String) null);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a critical state of the app.
     * @since 0.25
     */
    public static HealthCheckOutcome critical(String message) {
        return new HealthCheckOutcome(HealthCheckStatus.CRITICAL, message, null);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a critical state of the app.
     * @since 0.25
     */
    public static HealthCheckOutcome critical(Throwable th) {
        return new HealthCheckOutcome(HealthCheckStatus.CRITICAL, null, th);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a state of the app that can not be asserted.
     * @since 0.25
     */
    public static HealthCheckOutcome unknown() {
        return unknown((String) null);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a state of the app that can not be asserted.
     * @since 0.25
     */
    public static HealthCheckOutcome unknown(String message) {
        return new HealthCheckOutcome(HealthCheckStatus.UNKNOWN, message, null);
    }

    /**
     * @return a {@link HealthCheckOutcome} corresponding to a state of the app that can not be asserted.
     * @since 0.25
     */
    public static HealthCheckOutcome unknown(Throwable th) {
        return new HealthCheckOutcome(HealthCheckStatus.UNKNOWN, null, th);
    }

    /**
     * @return a {@link HealthCheckOutcome} with provided parameters.
     * @since 0.25
     */
    public static HealthCheckOutcome outcome(HealthCheckStatus status, String message, Throwable th) {
        return new HealthCheckOutcome(status, message, th);
    }

    /**
     * @return a new {@link HealthCheckOutcome} with all the information from this outcome plus extra metrics data
     * that was used to generate this outcome.
     * @since 0.26
     */
    public HealthCheckOutcome withData(HealthCheckData<?> data) {
        HealthCheckOutcome outcomeWithData = new HealthCheckOutcome(status, message, error);
        outcomeWithData.data = Optional.of(data);
        return outcomeWithData;
    }

    /**
     * Returns an optional extra data for this health check, that may assist the caller in making sense of the system
     * state. Kind of a metrics for health check.
     *
     * @return an optional extra data for this health check.
     * @since 0.26
     */
    public Optional<HealthCheckData<?>> getData() {
        return data;
    }

    /**
     * Compares this and another outcome by severity. Less severe outcomes are ordered prior to more severe ones.
     *
     * @param o another outcome to compare with.
     * @return an int according to {@link Comparable} contract.
     */
    @Override
    public int compareTo(HealthCheckOutcome o) {
        return status.getSeverity() - o.status.getSeverity();
    }

    public HealthCheckStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public String toString() {

        String message = this.message;

        if (message == null && error != null) {
            message = error.getMessage();
        }

        StringBuilder buffer = new StringBuilder().append("[").append(status.name());
        if (message != null) {
            buffer.append(": ").append(message);
        }

        return buffer.append("]").toString();
    }
}
