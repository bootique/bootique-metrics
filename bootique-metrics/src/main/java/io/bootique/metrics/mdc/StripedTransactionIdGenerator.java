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

package io.bootique.metrics.mdc;

import java.util.function.Supplier;

/**
 * A high-throughout {@link TransactionIdGenerator} that internally delegates calls to the per-thread id generators.
 */
public class StripedTransactionIdGenerator implements TransactionIdGenerator {

    private final int size;
    private final TransactionIdGenerator[] generators;


    public StripedTransactionIdGenerator(int size) {
        this(size, SafeTransactionIdGenerator::new);
    }

    public StripedTransactionIdGenerator(int size, Supplier<TransactionIdGenerator> generatorFactory) {
        this.size = size;
        this.generators = new SafeTransactionIdGenerator[size];

        for (int i = 0; i < size; i++) {
            generators[i] = generatorFactory.get();
        }
    }

    @Override
    public String nextId() {
        int generatorIndex = (int) (Thread.currentThread().getId() % size);
        return generators[generatorIndex].nextId();
    }
}
