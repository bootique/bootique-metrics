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

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Manages "txid" key in the SLF4J MDC.
 */
public class TransactionIdMDC {

    public static final String MDC_KEY = "txid";

    /**
     * Wraps a runnable with a code that initializes SLF4J MDC with the current transaction ID, and clears it after
     * the call. Intended to wrap parameters to ExecutorService.submit(..), CompletableFuture.runAsync(..) or similar
     * methods.
     *
     * @since 3.0
     */
    public static Runnable runnable(Runnable runnable) {
        String txId = getId();
        return txId != null ? new MDCRunnable(runnable, txId) : runnable;
    }

    /**
     * Wraps a runnable with a code that initializes SLF4J MDC with the current transaction ID, and clears it after
     * the call. Intended to wrap parameters to ExecutorService.submit(..), CompletableFuture.runAsync(..) or similar
     * methods.
     *
     * @since 3.0
     */
    public static <T> Callable<T> callable(Callable<T> callable) {
        String txId = getId();
        return txId != null ? new MDCCallable(callable, txId) : callable;
    }

    /**
     * Wraps a supplier with a code that initializes SLF4J MDC with the current transaction ID, and clears it after
     * the call. Intended to wrap parameters to CompletableFuture.supplyAsync(..) method or similar.
     *
     * @since 3.0
     */
    public static <T> Supplier<T> supplier(Supplier<T> supplier) {
        String txId = getId();
        return txId != null ? new MDCSupplier<>(supplier, txId) : supplier;
    }

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
}
