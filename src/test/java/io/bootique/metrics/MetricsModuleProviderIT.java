package io.bootique.metrics;

import io.bootique.test.junit.BQModuleProviderChecker;
import org.junit.Test;

public class MetricsModuleProviderIT {

	@Test
	public void testPresentInJar() {
		BQModuleProviderChecker.testPresentInJar(MetricsModuleProvider.class);
	}
}
