package io.bootique.metrics.mdc;

import org.slf4j.MDC;

/**
 * @since 0.25
 */
public class TransactionIdMDC {

    private static final String TRANSACTION_MDC_KEY = "txid";

    /**
     * Initializes SLF4J MDC with the current transaction ID.
     */
    public void reset(String transactionId) {
        if (transactionId == null) {
            MDC.remove(TRANSACTION_MDC_KEY);
        } else {
            MDC.put(TRANSACTION_MDC_KEY, transactionId);
        }
    }

    public String get() {
        return MDC.get(TRANSACTION_MDC_KEY);
    }

    /**
     * Removes transaction ID from the logging MDC.
     */
    public void clear() {
        MDC.remove(TRANSACTION_MDC_KEY);
    }
}
