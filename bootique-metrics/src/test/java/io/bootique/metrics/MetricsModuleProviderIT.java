package io.bootique.metrics;

import io.bootique.test.junit.BQModuleProviderChecker;
import org.junit.Test;

public class MetricsModuleProviderIT {

	@Test
	public void testAutoLoadable() {
		BQModuleProviderChecker.testAutoLoadable(MetricsModuleProvider.class);
	}

	@Test
	public void testMeta() {
		BQModuleProviderChecker.testMetadata(MetricsModuleProvider.class);
	}
}
