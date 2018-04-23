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
