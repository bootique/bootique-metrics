package io.bootique.metrics.health;

import io.bootique.BQRuntime;
import io.bootique.metrics.health.heartbeat.Heartbeat;
import io.bootique.metrics.health.heartbeat.HeartbeatListener;
import io.bootique.test.junit.BQTestFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HealthCheckModuleHeartbeatIT {

    @Rule
    public BQTestFactory testFactory = new BQTestFactory();

    private ThreadTester threadTester = new ThreadTester();
    private HealthCheck success;
    private HealthCheck failure;

    @Before
    public void before() {
        this.success = mock(HealthCheck.class);
        when(success.safeCheck()).thenReturn(HealthCheckOutcome.healthy());

        this.failure = mock(HealthCheck.class);
        when(failure.safeCheck()).thenReturn(HealthCheckOutcome.unhealthy("uh"));

        this.threadTester = new ThreadTester();
    }

    @Test
    public void testHeartbeat() throws InterruptedException {

        TestListener listener = new TestListener();

        BQRuntime runtime = testFactory.app("-c", "classpath:HealthCheckModuleHeartbeatIT.yml")
                .autoLoadModules()
                .module(b -> HealthCheckModule.extend(b)
                        .addHealthCheck("hc1", success)
                        .addHealthCheck("ignored", failure)
                        .addHeartbeatListener(listener))
                .createRuntime();

        threadTester.assertNoHeartbeat();

        Heartbeat hb = runtime.getInstance(Heartbeat.class);

        Thread.sleep(100);

        // not started yet...
        threadTester.assertNoHeartbeat();

        // start..
        hb.start();
        Thread.sleep(100);
        threadTester.assertHasHeartbeat();

        // we can't reliably predict the exact number of invocations at any given moment in the test, but we can
        // check that the heart is beating...

        int c1 = listener.counter;

        Thread.sleep(100);
        int c2 = listener.counter;
        assertTrue(c1 < c2);
        
        Thread.sleep(100);
        int c3 = listener.counter;
        assertTrue(c2 < c3);

        threadTester.assertPoolSize(2);

        runtime.shutdown();
        threadTester.assertNoHeartbeat();
    }

    private static class ThreadTester {


        private static final String TIMER_THREAD = "bootique-heartbeat";

        public void assertNoHeartbeat() {
            long matched = allThreads().filter(this::isTimerThread).count();
            assertEquals(0, matched);
        }

        public void assertHasHeartbeat() {
            long matched = allThreads().filter(this::isTimerThread).count();
            assertEquals(1, matched);
        }

        public void assertPoolSize(int expected) {
            long matched = allThreads().filter(this::isPoolThread).count();
            assertEquals(expected, matched);
        }

        private boolean isTimerThread(Thread t) {
            // the name comes from HeartbeatLauncher
            return "bootique-heartbeat".equals(t.getName());
        }

        private boolean isPoolThread(Thread t) {
            // the name comes from HeartbeatFactory
            return t.getName().startsWith("bootique-healthcheck-");
        }

        private Stream<Thread> allThreads() {
            ThreadGroup tg = Thread.currentThread().getThreadGroup();
            while (tg.getParent() != null) {
                tg = tg.getParent();
            }

            Thread[] active = new Thread[tg.activeCount()];
            tg.enumerate(active);
            return Arrays.stream(active);
        }
    }

    class TestListener implements HeartbeatListener {

        int counter;

        @Override
        public void healthChecksFinished(Map<String, HealthCheckOutcome> result) {
            counter++;

            // check filtering
            assertTrue(result.containsKey("hc1"));
            assertFalse(result.containsKey("ignored"));
        }
    }

}
