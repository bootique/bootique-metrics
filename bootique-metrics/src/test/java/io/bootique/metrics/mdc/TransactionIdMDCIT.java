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

import io.bootique.BQCoreModule;
import io.bootique.BQRuntime;
import io.bootique.Bootique;
import io.bootique.junit5.BQApp;
import io.bootique.junit5.BQTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@BQTest
public class TransactionIdMDCIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionIdMDCIT.class);

    // setup a Bootique app that would configure logback MDC info
    @BQApp(skipRun = true)
    static final BQRuntime app = Bootique.app()
            .autoLoadModules()
            .module(b -> BQCoreModule.extend(b).setProperty("bq.log.logFormat", "[%date{\"dd/MMM/yyyy:HH:mm:ss,SSS\"}] %thread %level %mdc{txid:-?} %logger{1}: %msg%n%ex"))
            .createRuntime();

    @Test
    public void runnable() throws ExecutionException, InterruptedException {
        AtomicReference<String> txId = new AtomicReference<>();


        TransactionIdMDC.setId("_TXID_");
        Runnable toTest = TransactionIdMDC.runnable(() -> {
            LOGGER.info("within tx");
            txId.set(TransactionIdMDC.getId());
        });

        ExecutorService pool = Executors.newFixedThreadPool(3);
        try {
            pool.submit(toTest).get();
        } finally {
            pool.shutdown();
            TransactionIdMDC.clearId();
        }

        assertEquals("_TXID_", txId.get());
    }

    @Test
    public void callable() throws ExecutionException, InterruptedException {
        AtomicReference<String> txId = new AtomicReference<>();

        TransactionIdMDC.setId("_TXID_");
        Callable<String> toTest = TransactionIdMDC.callable(() -> {
            LOGGER.info("within tx");
            txId.set(TransactionIdMDC.getId());
            return "a";
        });

        ExecutorService pool = Executors.newFixedThreadPool(3);

        try {
            assertEquals("a", pool.submit(toTest).get());
        } finally {
            pool.shutdown();
            TransactionIdMDC.clearId();
        }

        assertEquals("_TXID_", txId.get());
    }

    @Test
    public void supplier() throws ExecutionException, InterruptedException {
        AtomicReference<String> txId = new AtomicReference<>();

        TransactionIdMDC.setId("_TXID_");
        Supplier<String> toTest = TransactionIdMDC.supplier(() -> {
            LOGGER.info("within tx");
            txId.set(TransactionIdMDC.getId());
            return "a";
        });

        try {
            assertEquals("a", CompletableFuture.supplyAsync(toTest).get());
        } finally {
            TransactionIdMDC.clearId();
        }

        assertEquals("_TXID_", txId.get());
    }

    @Test
    public void runnable_NoMDC() {
        Runnable r = () -> {
        };
        assertSame(r, TransactionIdMDC.runnable(r));
    }

    @Test
    public void supplier_NoMDC() {
        Supplier<String> s = () -> "a";
        assertSame(s, TransactionIdMDC.supplier(s));
    }

    @Test
    public void callable_NoMDC() {
        Callable<String> s = () -> "a";
        assertSame(s, TransactionIdMDC.callable(s));
    }

    @Test
    public void runnable_DontOverrideExisting() throws ExecutionException, InterruptedException {
        AtomicReference<String> txId = new AtomicReference<>();

        TransactionIdMDC.setId("_TXID_");

        Runnable toTest = TransactionIdMDC.runnable(() -> {
            LOGGER.info("within tx");
            txId.set(TransactionIdMDC.getId());
        });

        ExecutorService pool = Executors.newFixedThreadPool(3);

        try {
            pool.submit(() -> {
                TransactionIdMDC.setId("_OTHER_TXID_");
                toTest.run();
                TransactionIdMDC.clearId();
            }).get();
        } finally {
            pool.shutdown();
            TransactionIdMDC.clearId();
        }

        assertEquals("_OTHER_TXID_", txId.get());
    }
}
