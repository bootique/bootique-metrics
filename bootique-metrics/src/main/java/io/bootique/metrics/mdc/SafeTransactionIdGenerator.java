package io.bootique.metrics.mdc;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An overflow-free implementation of {@link TransactionIdGenerator}.
 *
 * @since 0.25
 */
public class SafeTransactionIdGenerator implements TransactionIdGenerator {

    private final int counterStart;
    private final ReentrantLock delegateResetLock;
    private volatile UnsafeTransactionIdGenerator delegate;

    public SafeTransactionIdGenerator() {
        this(Integer.MIN_VALUE);
    }

    public SafeTransactionIdGenerator(int counterStart) {
        this.counterStart = counterStart;
        this.delegate = createDelegate();
        this.delegateResetLock = new ReentrantLock();
    }

    @Override
    public String nextId() {
        resetIfNeeded();
        return delegate.nextId();
    }

    protected UnsafeTransactionIdGenerator createDelegate() {
        return new UnsafeTransactionIdGenerator(counterStart);
    }

    void resetIfNeeded() {
        if (delegate.willNeedResetSoon()) {

            // TODO: use hard reset with blocking if background tasks did not finish by the time the app was able to churn
            // through 1mln IDs. Standalone generator produced 34mln ids / sec in one of the benchmarks in 2017, so
            // this condition is not completely out of the question. Note that the overflow will not cause any exceptions,
            // but the AtomicInteger will simply restrat the counter from Integer.MIN_VALUE, potentially causing ID
            // duplicates
            
            ForkJoinPool.commonPool().submit(() -> reset());
        }
    }

    void reset() {

        // no need for multiple threads to do reset, so abandon the attempt if another thread owns the lock
        if (delegateResetLock.tryLock()) {
            try {
                if (delegate.willNeedResetSoon()) {
                    this.delegate = createDelegate();
                }
            } finally {
                delegateResetLock.unlock();
            }
        }
    }

}
