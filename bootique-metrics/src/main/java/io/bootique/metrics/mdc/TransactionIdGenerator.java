package io.bootique.metrics.mdc;


/**
 * Generates fast and mostly unique IDs for the purpose of "business transactions" correlation in the logs. A business
 * transaction may be a web request, a job execution, etc. Since correlation is the main use case here, each ID doesn't
 * have to be globally unique forever and doesn't have to be hard to guess. So implementing generators are normally
 * optimized for performance instead of randomness, uniqueness and security.
 *
 * @since 0.25
 */
public interface TransactionIdGenerator {

    String nextId();
}
