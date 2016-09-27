package io.bootique.metrics.web;

import io.bootique.test.junit.BQModuleProviderChecker;
import org.junit.Test;

public class MetricsWebModuleProviderTest {

    @Test
    public void testAutoLoad() {
        BQModuleProviderChecker.testPresentInJar(MetricsWebModuleProvider.class);
    }
}
