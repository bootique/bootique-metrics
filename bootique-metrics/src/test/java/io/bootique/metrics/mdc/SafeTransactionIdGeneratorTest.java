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

        // the next ID after reset still follows the initial sequence...
        String id2 = generator.nextId();

        assertNotEquals(id1, id2);
        assertEquals(
                id1.substring(0, UnsafeTransactionIdGenerator.COUNTER_STRING_LEN),
                id2.substring(0, UnsafeTransactionIdGenerator.COUNTER_STRING_LEN));

        // wait until background thread resets the id...
        Thread.sleep(500);

        String id3 = generator.nextId();

        assertNotEquals(
                id1.substring(0, UnsafeTransactionIdGenerator.COUNTER_STRING_LEN),
                id3.substring(0, UnsafeTransactionIdGenerator.COUNTER_STRING_LEN));
    }
}
