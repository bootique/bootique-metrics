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

import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link TransactionIdGenerator} that needs to be checked for possible overflow by the caller.
 *
 * @since 0.25
 */
public class UnsafeTransactionIdGenerator implements TransactionIdGenerator {

    // add some room before overflow happens, so that reset could be processed on background...
    static final int RESET_THRESHOLD = Integer.MAX_VALUE - 1_000_000;
    static final int COUNTER_STRING_LEN = 8;
    static final int STRING_LENGTH = 8 + COUNTER_STRING_LEN;
    static final String PADDING = "00000000";

    private String base;
    private AtomicInteger counter;

    public UnsafeTransactionIdGenerator(int counterStart) {
        byte[] randomBytes = new byte[5];
        new Random().nextBytes(randomBytes);
        String base = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        this.base = base + "-";
        this.counter = new AtomicInteger(counterStart);
    }

    public boolean willNeedResetSoon() {
        return counter.get() >= RESET_THRESHOLD;
    }

    @Override
    public String nextId() {

        int next = counter.getAndIncrement();
        String nextString = Integer.toHexString(next);

        return new StringBuilder(STRING_LENGTH)
                .append(base)
                .append(PADDING.substring(0, COUNTER_STRING_LEN - nextString.length()))
                .append(nextString)
                .toString();
    }
}
