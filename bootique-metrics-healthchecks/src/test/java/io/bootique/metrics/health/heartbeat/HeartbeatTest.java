package io.bootique.metrics.health.heartbeat;

import org.junit.Test;

import java.util.Timer;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class HeartbeatTest {

    @Test
    public void testStartStop() {

        Timer mockTimer = mock(Timer.class);

        Heartbeat hb = new Heartbeat(() -> mockTimer);
        verifyZeroInteractions(mockTimer);

        hb.start();
        verifyZeroInteractions(mockTimer);
        assertSame(mockTimer, hb.heartbeatTimer);

        hb.stop();
        verify(mockTimer).cancel();
    }
}
