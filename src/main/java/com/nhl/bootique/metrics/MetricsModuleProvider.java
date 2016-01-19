package com.nhl.bootique.metrics;

import com.google.inject.Module;
import com.nhl.bootique.BQModuleProvider;

public class MetricsModuleProvider implements BQModuleProvider {

	@Override
	public Module module() {
		return new MetricsModule();
	}
}
