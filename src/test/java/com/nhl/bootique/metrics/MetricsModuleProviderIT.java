package com.nhl.bootique.metrics;

import com.nhl.bootique.test.junit.BQModuleProviderChecker;
import org.junit.Test;

public class MetricsModuleProviderIT {

	@Test
	public void testPresentInJar() {
		BQModuleProviderChecker.testPresentInJar(MetricsModuleProvider.class);
	}
}
