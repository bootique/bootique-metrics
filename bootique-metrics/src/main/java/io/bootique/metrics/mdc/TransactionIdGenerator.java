package io.bootique.metrics.mdc;

import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Generates fast and mostly unique IDs for the purpose of "business transactions" correlation in the logs. Business
 * transaction may be a web request, a job execution, etc. Since correlation is the main use case here, each ID doesn't
 * have to be globally unique forever or doesn't have to be hard to guess. So the generator is optimized for
 * performance instead of randomness, uniqueness and security.
 *
 * @since 0.25
 */
public class TransactionIdGenerator {

    // add some room before overflow, so that reset could proceed on background...
    static final int RESET_THRESHOLD = Integer.MAX_VALUE - 100000;
    static final int COUNTER_STRING_LEN = 8;
    static final int STRING_LENGTH = 8 + COUNTER_STRING_LEN;
    static final String PADDING = "00000000";

    private final ReentrantLock resetLock;
    private final int counterStart;
    private volatile AtomicTransactionIdGenerator delegate;

    public TransactionIdGenerator() {
        this(Integer.MIN_VALUE);
    }

    public TransactionIdGenerator(int counterStart) {
        this.counterStart = counterStart;
        this.resetLock = new ReentrantLock();
        this.delegate = new AtomicTransactionIdGenerator(counterStart);
    }

    public String nextId() {
        resetIfNeeded();
        return delegate.nextId();
    }

    void resetIfNeeded() {
        if (delegate.willNeedResetSoon()) {
            // reset on background...
            // TODO: use hard reset if background tasks did not succeed, and the app was able to churn through 100K IDs.
            ForkJoinPool.commonPool().submit(() -> reset());
        }
    }

    void reset() {

        // no need for multiple threads to do reset, so abandon the attempt if another thread owns the lock
        if (resetLock.tryLock()) {
            try {
                if (delegate.willNeedResetSoon()) {
                    this.delegate = new AtomicTransactionIdGenerator(counterStart);
                }
            } finally {
                resetLock.unlock();
            }
        }
    }

    final class AtomicTransactionIdGenerator {


        private String base;
        private AtomicInteger counter;

        public AtomicTransactionIdGenerator(int counterStart) {
            byte[] randomBytes = new byte[5];
            new Random().nextBytes(randomBytes);
            String base = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

            this.base = base + "-";
            this.counter = new AtomicInteger(counterStart);
        }

        public boolean willNeedResetSoon() {
            return counter.get() >= RESET_THRESHOLD;
        }

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
}
