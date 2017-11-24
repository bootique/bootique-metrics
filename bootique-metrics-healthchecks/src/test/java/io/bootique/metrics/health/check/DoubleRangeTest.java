package io.bootique.metrics.health.check;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DoubleRangeTest {

    @Test
    public void testParse_Null() {
        DoubleRange r = DoubleRange.parse(null);
        assertNotNull(r);
        assertNull(r.getWarningThreshold());
        assertNull(r.getCriticalThreshold());
    }

    @Test
    public void testParse_Empty() {
        DoubleRange r = DoubleRange.parse("");
        assertNotNull(r);
        assertNull(r.getWarningThreshold());
        assertNull(r.getCriticalThreshold());
    }

    @Test
    public void testParse_Critical() {
        DoubleRange r = DoubleRange.parse("4.1");
        assertNotNull(r);
        assertNull(r.getWarningThreshold());
        assertEquals(4.1, r.getCriticalThreshold().doubleValue(), 0.00001);
    }

    @Test
    public void testParse_WarningCritical() {
        DoubleRange r = DoubleRange.parse(" 3.55  4 ");
        assertNotNull(r);
        assertEquals(3.55, r.getWarningThreshold().doubleValue(), 0.00001);
        assertEquals(4.0, r.getCriticalThreshold().doubleValue(), 0.00001);
    }

    @Test(expected = RuntimeException.class)
    public void testParse_InvalidNumber() {
        DoubleRange.parse(" 3.6 a");
    }

    @Test(expected = RuntimeException.class)
    public void testParse_InvalidThresholds() {
        DoubleRange.parse(" 3 4 5");
    }

    @Test(expected = RuntimeException.class)
    public void testParse_InvalidRelativeThresholds() {
        DoubleRange.parse(" 4 3");
    }

    @Test
    public void testParse_FromJson() throws IOException {

        YAMLParser parser = new YAMLFactory().createParser("3.5 4.5");
        DoubleRange r = new ObjectMapper().readValue(parser, DoubleRange.class);

        assertNotNull(r);
        assertEquals(3.5, r.getWarningThreshold().doubleValue(), 0.00001);
        assertEquals(4.5, r.getCriticalThreshold().doubleValue(), 0.00001);
    }
}
