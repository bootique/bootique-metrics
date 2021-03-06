/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.metrics.health.heartbeat;

import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class HeartbeatTest {

    @Test
    public void testStartStop() {

        HeartbeatWatch mockStopper = mock(HeartbeatWatch.class);

        Heartbeat hb = new Heartbeat(() -> mockStopper);
        verifyNoMoreInteractions(mockStopper);

        hb.start();
        verifyNoMoreInteractions(mockStopper);
        assertSame(mockStopper, hb.heartbeatWatch);

        hb.stop();
        verify(mockStopper).stop();
    }
}
