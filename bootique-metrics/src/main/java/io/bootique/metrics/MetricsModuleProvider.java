package io.bootique.metrics;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;

public class MetricsModuleProvider implements BQModuleProvider {

	@Override
	public Module module() {
		return new MetricsModule();
	}
}
