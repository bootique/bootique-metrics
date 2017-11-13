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

    // add some room before overflow, so that reset could proceed on background...
    static final int RESET_THRESHOLD = Integer.MAX_VALUE - 100000;
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
