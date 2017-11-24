package io.bootique.metrics.health.check;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import io.bootique.metrics.health.HealthCheckStatus;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class IntRangeTest {

    @Test
    public void testClassify() {

        IntRange range = new IntRange(5, 8);

        assertEquals(HealthCheckStatus.OK, range.classify(-1));
        assertEquals(HealthCheckStatus.OK, range.classify(0));
        assertEquals(HealthCheckStatus.WARNING, range.classify(5));
        assertEquals(HealthCheckStatus.WARNING, range.classify(6));
        assertEquals(HealthCheckStatus.CRITICAL, range.classify(9));
    }

    @Test
    public void testParse_Null() {
        IntRange r = IntRange.parse(null);
        assertNotNull(r);
        assertNull(r.getWarningThreshold());
        assertNull(r.getCriticalThreshold());
    }

    @Test
    public void testParse_Empty() {
        IntRange r = IntRange.parse("");
        assertNotNull(r);
        assertNull(r.getWarningThreshold());
        assertNull(r.getCriticalThreshold());
    }

    @Test
    public void testParse_Critical() {
        IntRange r = IntRange.parse("4");
        assertNotNull(r);
        assertNull(r.getWarningThreshold());
        assertEquals(Integer.valueOf(4), r.getCriticalThreshold());
    }

    @Test
    public void testParse_WarningCritical() {
        IntRange r = IntRange.parse(" 3,  4 ");
        assertNotNull(r);
        assertEquals(Integer.valueOf(3), r.getWarningThreshold());
        assertEquals(Integer.valueOf(4), r.getCriticalThreshold());
    }

    @Test(expected = RuntimeException.class)
    public void testParse_InvalidNumber() {
        IntRange.parse(" 3, a");
    }

    @Test(expected = RuntimeException.class)
    public void testParse_InvalidThresholds() {
        IntRange.parse(" 3, 4, 5");
    }

    @Test(expected = RuntimeException.class)
    public void testParse_InvalidRelativeThresholds() {
        IntRange.parse(" 4, 3");
    }

    @Test
    public void testParse_FromJson() throws IOException {

        YAMLParser parser = new YAMLFactory().createParser("3, 4");
        IntRange r = new ObjectMapper().readValue(parser, IntRange.class);

        assertNotNull(r);
        assertEquals(Integer.valueOf(3), r.getWarningThreshold());
        assertEquals(Integer.valueOf(4), r.getCriticalThreshold());
    }
}
