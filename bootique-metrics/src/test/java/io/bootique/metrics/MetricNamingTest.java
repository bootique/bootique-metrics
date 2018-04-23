package io.bootique.metrics;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MetricNamingTest {

    @Test
    public void testFromParts() {
        assertEquals("", MetricNaming.fromParts());
        assertEquals("", MetricNaming.fromParts(""));
        assertEquals("a.bc.d", MetricNaming.fromParts("a", "bc", "d"));
    }

    @Test
    public void testName_NoInstance() {
        assertEquals("bq.MetricNamingTestX.M1.N1", MetricNaming.name(MetricNamingTestXModule.class, "M1", "N1"));
        assertEquals("bq.MetricNamingTestX.M1.N1", MetricNaming.name(MetricNamingTestXInstrumentedModule.class, "M1", "N1"));
    }

    @Test
    public void testName_Instance() {
        assertEquals("bq.MetricNamingTestX.M1.I1.N1", MetricNaming.name(MetricNamingTestXModule.class,
                "M1", "I1", "N1"));
        assertEquals("bq.MetricNamingTestX.M1.I1.N1", MetricNaming.name(MetricNamingTestXInstrumentedModule.class,
                "M1", "I1", "N1"));
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
