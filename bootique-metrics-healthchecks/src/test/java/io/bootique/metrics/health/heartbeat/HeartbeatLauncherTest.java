package io.bootique.metrics.health.heartbeat;

import io.bootique.metrics.health.HealthCheck;
import io.bootique.metrics.health.HealthCheckOutcome;
import io.bootique.metrics.health.HealthCheckRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HeartbeatLauncherTest {

    private HealthCheck success;
    private HealthCheck failure;
    private ExecutorService threadPool;
    private Timer timer;

    @Before
    public void before() {
        this.success = mock(HealthCheck.class);
        when(success.safeCheck()).thenReturn(HealthCheckOutcome.ok());

        this.failure = mock(HealthCheck.class);
        when(failure.safeCheck()).thenReturn(HealthCheckOutcome.critical("uh"));

        this.timer = null;
        this.threadPool = null;
    }

    @After
    public void after() {
        if (timer != null) {
            timer.cancel();
        }

        if (threadPool != null) {
            threadPool.shutdownNow();
        }
    }


    @Test
    public void testStart() throws InterruptedException {

        TestListener listener = new TestListener();

        HealthCheckRegistry registry = createRegistry(success, failure);

        this.threadPool = Executors.newFixedThreadPool(2);
        this.timer = new HeartbeatLauncher(registry)
                .initialDelayMs(3)
                .fixedDelayMs(50)
                .listener(listener)
                .start();

        // we can't reliably predict the exact number of invocations at any given moment in the test, but we can
        // check that the heart is beating...

        int c1 = listener.counter;

        Thread.sleep(100);
        int c2 = listener.counter;
        assertTrue(c1 < c2);

        Thread.sleep(100);
        int c3 = listener.counter;
        assertTrue(c2 < c3);
    }

    private HealthCheckRegistry createRegistry(HealthCheck... checks) {

        Map<String, HealthCheck> healthChecks = new HashMap<>();
        for (int i = 0; i < checks.length; i++) {
            healthChecks.put(String.valueOf(i), checks[i]);
        }

        return new HealthCheckRegistry(healthChecks);
    }

    class TestListener implements HeartbeatListener {

        int counter;

        @Override
        public void healthChecksFinished(Map<String, HealthCheckOutcome> result) {
            counter++;
        }
    }
}
