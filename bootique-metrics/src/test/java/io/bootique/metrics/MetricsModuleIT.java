package io.bootique.metrics;

import com.codahale.metrics.MetricRegistry;
import io.bootique.BQRuntime;
import io.bootique.metrics.reporter.JmxReporterFactory;
import io.bootique.metrics.reporter.Slf4jReporterFactory;
import io.bootique.test.junit.BQTestFactory;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MetricsModuleIT {

    @Rule
    public final BQTestFactory testFactory = new BQTestFactory();

    protected BQRuntime createRuntime(String... args) {
        return testFactory.app(args).module(MetricsModule.class).createRuntime();
    }

    @Test
    public void testMetricRegistryConfig() {

        BQRuntime runtime = createRuntime("--config", "classpath:config1.yml");
        MetricRegistryFactory factory = runtime.getInstance(MetricRegistryFactory.class);

        assertEquals(3, factory.getReporters().size());
        assertTrue(factory.getReporters().get(0) instanceof Slf4jReporterFactory);
        assertTrue(factory.getReporters().get(1) instanceof JmxReporterFactory);
        assertTrue(factory.getReporters().get(2) instanceof Slf4jReporterFactory);
    }

    @Test
    public void testMetricRegistry() {

        BQRuntime runtime = createRuntime();

        MetricRegistry r1 = runtime.getInstance(MetricRegistry.class);
        MetricRegistry r2 = runtime.getInstance(MetricRegistry.class);
        assertNotNull(r1);
        assertSame("MetricRegistry must be a singleton", r1, r2);
    }
}
