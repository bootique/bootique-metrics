/**
 *    Licensed to the ObjectStyle LLC under one
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SafeTransactionIdGeneratorTest {

    @Test
    public void testNextId() {

        SafeTransactionIdGenerator generator = new SafeTransactionIdGenerator();

        String id1 = generator.nextId();
        assertEquals(UnsafeTransactionIdGenerator.STRING_LENGTH, id1.length());

        String id2 = generator.nextId();
        assertEquals(UnsafeTransactionIdGenerator.STRING_LENGTH, id2.length());

        assertNotEquals(id1, id2);
        assertEquals(
                id1.substring(0, UnsafeTransactionIdGenerator.COUNTER_STRING_LEN),
                id2.substring(0, UnsafeTransactionIdGenerator.COUNTER_STRING_LEN));
    }

    @Test
    public void testNextId_MidRange() {

        SafeTransactionIdGenerator generator = new SafeTransactionIdGenerator(-1);

        String id1 = generator.nextId();
        assertEquals(UnsafeTransactionIdGenerator.STRING_LENGTH, id1.length());

        String id2 = generator.nextId();
        assertEquals(UnsafeTransactionIdGenerator.STRING_LENGTH, id2.length());

        assertNotEquals(id1, id2);
        assertEquals(
                id1.substring(0, UnsafeTransactionIdGenerator.COUNTER_STRING_LEN),
                id2.substring(0, UnsafeTransactionIdGenerator.COUNTER_STRING_LEN));
    }

    @Test
    public void testNextId_Overflow() throws InterruptedException {

        SafeTransactionIdGenerator generator = new SafeTransactionIdGenerator(UnsafeTransactionIdGenerator.RESET_THRESHOLD - 1);

        String id1 = generator.nextId();

        // generate a few more ids and wait till the reset happens on background
        generator.nextId();
        generator.nextId();
        Thread.sleep(500);

        String id2 = generator.nextId();

        assertNotEquals(
                id1.substring(0, UnsafeTransactionIdGenerator.COUNTER_STRING_LEN),
                id2.substring(0, UnsafeTransactionIdGenerator.COUNTER_STRING_LEN));
    }
}
