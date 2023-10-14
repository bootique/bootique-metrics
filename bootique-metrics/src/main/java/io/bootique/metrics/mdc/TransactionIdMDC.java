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

package io.bootique.metrics.mdc;

import org.slf4j.MDC;

/**
 * Manages "txid" key in the SLF4J MDC.
 */
public class TransactionIdMDC {

    public static final String MDC_KEY = "txid";

    /**
     * Initializes SLF4J MDC with the current transaction ID.
     *
     * @since 3.0
     */
    public static void setId(String transactionId) {
        if (transactionId == null) {
            MDC.remove(MDC_KEY);
        } else {
            MDC.put(MDC_KEY, transactionId);
        }
    }

    /**
     * Returns transaction ID for the current thread.
     *
     * @since 3.0
     */
    public static String getId() {
        return MDC.get(MDC_KEY);
    }


    /**
     * Removes transaction ID from the logging MDC.
     *
     * @since 3.0
     */
    public static void clearId() {
        MDC.remove(MDC_KEY);
    }

    /**
     * Initializes SLF4J MDC with the current transaction ID.
     *
     * @deprecated in favor of static {@link #setId(String)}
     */
    @Deprecated(since = "3.0")
    public void reset(String transactionId) {
        TransactionIdMDC.setId(transactionId);
    }

    /**
     * @deprecated since 3.0 in favor of the static {@link #getId()}
     */
    @Deprecated(since = "3.0")
    public String get() {
        return getId();
    }

    /**
     * @deprecated since 3.0 in favor of the static {@link #clearId()}
     */
    @Deprecated(since = "3.0")
    public void clear() {
        clearId();
    }
}
