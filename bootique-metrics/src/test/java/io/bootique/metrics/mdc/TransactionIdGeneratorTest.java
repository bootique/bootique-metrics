package io.bootique.metrics.mdc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TransactionIdGeneratorTest {

    @Test
    public void testNextId() {

        TransactionIdGenerator generator = new TransactionIdGenerator();

        String id1 = generator.nextId();
        assertEquals(TransactionIdGenerator.STRING_LENGTH, id1.length());

        String id2 = generator.nextId();
        assertEquals(TransactionIdGenerator.STRING_LENGTH, id2.length());

        assertNotEquals(id1, id2);
        assertEquals(
                id1.substring(0, TransactionIdGenerator.COUNTER_STRING_LEN),
                id2.substring(0, TransactionIdGenerator.COUNTER_STRING_LEN));
    }

    @Test
    public void testNextId_MidRange() {

        TransactionIdGenerator generator = new TransactionIdGenerator(-1);

        String id1 = generator.nextId();
        assertEquals(TransactionIdGenerator.STRING_LENGTH, id1.length());

        String id2 = generator.nextId();
        assertEquals(TransactionIdGenerator.STRING_LENGTH, id2.length());

        assertNotEquals(id1, id2);
        assertEquals(
                id1.substring(0, TransactionIdGenerator.COUNTER_STRING_LEN),
                id2.substring(0, TransactionIdGenerator.COUNTER_STRING_LEN));
    }

    @Test
    public void testNextId_Overflow() throws InterruptedException {

        TransactionIdGenerator generator = new TransactionIdGenerator(TransactionIdGenerator.RESET_THRESHOLD - 1);

        String id1 = generator.nextId();

        // the next ID after reset still follows the initial sequence...
        String id2 = generator.nextId();

        assertNotEquals(id1, id2);
        assertEquals(
                id1.substring(0, TransactionIdGenerator.COUNTER_STRING_LEN),
                id2.substring(0, TransactionIdGenerator.COUNTER_STRING_LEN));

        // wait until background thread resets the id...
        Thread.sleep(500);

        String id3 = generator.nextId();

        assertNotEquals(
                id1.substring(0, TransactionIdGenerator.COUNTER_STRING_LEN),
                id3.substring(0, TransactionIdGenerator.COUNTER_STRING_LEN));
    }
}
