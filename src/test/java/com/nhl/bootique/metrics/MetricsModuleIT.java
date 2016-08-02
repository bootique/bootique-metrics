package com.nhl.bootique.metrics;

import com.nhl.bootique.BQRuntime;
import com.nhl.bootique.Bootique;
import com.nhl.bootique.metrics.reporter.JmxReporterFactory;
import com.nhl.bootique.metrics.reporter.Slf4jReporterFactory;
import com.nhl.bootique.test.junit.BQTestFactory;
import org.junit.Rule;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MetricsModuleIT {

    @Rule
    public final BQTestFactory testFactory = new BQTestFactory();

    @Test
    public void testConfig() {

        Consumer<Bootique> config = b -> {
            b.module(MetricsModule.class);
        };

        BQRuntime runtime = testFactory.newRuntime().configurator(config).build("--config", "classpath:config1.yml").getRuntime();
        MetricRegistryFactory factory = runtime.getInstance(MetricRegistryFactory.class);

        assertEquals(3, factory.getReporters().size());
        assertTrue(factory.getReporters().get(0) instanceof Slf4jReporterFactory);
        assertTrue(factory.getReporters().get(1) instanceof JmxReporterFactory);
        assertTrue(factory.getReporters().get(2) instanceof Slf4jReporterFactory);
    }
}
