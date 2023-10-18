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
package io.bootique.metrics.health;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

public class HealthCheckExecutor {
    private final CountDownLatch doneSignal;
    private final ExecutorService threadPool;
    private final Map<String, HealthCheck> activeChecks;

    public HealthCheckExecutor(Map<String, HealthCheck> activeChecks, ExecutorService threadPool) {
        this.doneSignal = new CountDownLatch(activeChecks.size());
        this.threadPool = new HealthCheckExecutorService(doneSignal, threadPool);
        this.activeChecks = activeChecks;
    }

    public Map<String, HealthCheckOutcome> runChecks(long timeout, TimeUnit timeoutUnit) {
        // use the latch to ensure we can control the overall timeout, not individual health check timeouts...
        // note that if the health check pool is thread-starved, then a few slow checks would result in
        // faster checks reported as timeouts...
        Map<String, Future<HealthCheckOutcome>> futures = new HashMap<>();
        activeChecks.forEach((n, hc) -> futures.put(n, threadPool.submit(hc::safeCheck)));
        try {
            doneSignal.await(timeout, timeoutUnit);
        } catch (InterruptedException e) {
            // let's still finish the health check analysis on interrupt
        }

        Map<String, HealthCheckOutcome> results = new HashMap<>();
        futures.forEach((n, f) -> results.put(n, immediateOutcome(f)));
        return results;
    }

    private static HealthCheckOutcome immediateOutcome(Future<HealthCheckOutcome> hcRunner) {
        if (hcRunner.isDone()) {
            try {
                return hcRunner.get();
            } catch (Exception e) {
                // unexpected... we should be done here...
                return HealthCheckOutcome.critical(e);
            }
        } else {
            return HealthCheckOutcome.critical("health check timed out");
        }
    }

    static class HealthCheckExecutorService extends AbstractExecutorService {
        private final CountDownLatch doneSignal;
        private final ExecutorService threadPool;

        HealthCheckExecutorService(CountDownLatch doneSignal, ExecutorService threadPool) {
            this.doneSignal = doneSignal;
            this.threadPool = threadPool;
        }

        @Override
        protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
            return new CountDownFuture<>(doneSignal, callable);
        }

        @Override
        protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
            return new CountDownFuture<T>(doneSignal, runnable, value);
        }

        @Override
        public void execute(Runnable command) {
            threadPool.execute(command);
        }

        @Override
        public void shutdown() {
            threadPool.shutdown();
        }

        @Override
        public List<Runnable> shutdownNow() {
            return threadPool.shutdownNow();
        }

        @Override
        public boolean isShutdown() {
            return threadPool.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return threadPool.isTerminated();
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return threadPool.awaitTermination(timeout, unit);
        }
    }

    static class CountDownFuture<T> extends FutureTask<T> {

        private final CountDownLatch doneSignal;

        public CountDownFuture(CountDownLatch doneSignal, Callable<T> callable) {
            super(callable);
            this.doneSignal = doneSignal;
        }

        public CountDownFuture(CountDownLatch doneSignal, Runnable runnable, T value) {
            super(runnable, value);
            this.doneSignal = doneSignal;
        }

        @Override
        protected void done() {
            super.done();
            doneSignal.countDown();
        }
    }
}
