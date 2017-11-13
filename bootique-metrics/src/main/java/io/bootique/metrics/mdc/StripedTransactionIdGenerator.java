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
