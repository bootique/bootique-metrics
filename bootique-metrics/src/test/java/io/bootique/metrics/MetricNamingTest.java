/**
 *  Licensed to ObjectStyle LLC under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ObjectStyle LLC licenses
 *  this file to you under the Apache License, Version 2.0 (the
 *  “License”); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.bootique.metrics;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MetricNamingTest {

    @Test
    public void testName() {

        assertEquals("bq.MetricNamingTestX", MetricNaming
                .forModule(MetricNamingTestXModule.class)
                .name());

        assertEquals("bq.MetricNamingTestX.M1", MetricNaming
                .forModule(MetricNamingTestXModule.class)
                .name("M1"));

        assertEquals("bq.MetricNamingTestX.M1.I1.N1", MetricNaming
                .forModule(MetricNamingTestXModule.class)
                .name("M1", "I1", "N1"));

        assertEquals("bq.MetricNamingTestX.M1.I1.N1", MetricNaming
                .forModule(MetricNamingTestXInstrumentedModule.class)
                .name("M1", "I1", "N1"));
    }

}

class MetricNamingTestXModule implements Module {
    @Override
    public void configure(Binder binder) {

    }
}

class MetricNamingTestXInstrumentedModule implements Module {
    @Override
    public void configure(Binder binder) {

    }
}
