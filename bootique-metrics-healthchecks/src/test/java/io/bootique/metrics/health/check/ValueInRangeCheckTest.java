package io.bootique.metrics.health.check;

import io.bootique.metrics.health.HealthCheckStatus;
import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValueInRangeCheckTest {

    @Test
    public void testCheck() {

        Supplier<Integer> supplier = mock(Supplier.class);
        when(supplier.get()).thenReturn(-1, 0, 5, 6, 9);

        IntRange range = new IntRange(5, 8);
        ValueInRangeCheck<Integer> check = new ValueInRangeCheck<>(range, supplier);

        assertEquals(HealthCheckStatus.OK, check.check().getStatus());
        assertEquals(HealthCheckStatus.OK, check.check().getStatus());
        assertEquals(HealthCheckStatus.WARNING, check.check().getStatus());
        assertEquals(HealthCheckStatus.WARNING, check.check().getStatus());
        assertEquals(HealthCheckStatus.CRITICAL, check.check().getStatus());
    }
}
