package io.bootique.metrics.health.heartbeat;

import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class HeartbeatTest {

    @Test
    public void testStartStop() {

        Runnable mockStopper = mock(Runnable.class);

        Heartbeat hb = new Heartbeat(() -> mockStopper);
        verifyZeroInteractions(mockStopper);

        hb.start();
        verifyZeroInteractions(mockStopper);
        assertSame(mockStopper, hb.heartbeatStopper);

        hb.stop();
        verify(mockStopper).run();
    }
}
