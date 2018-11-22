package io.bootique.metrics.health.writer;

import io.bootique.value.Percent;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * @since 1.0.RC1
 */
public class PercentConverter implements ValueConverter<Percent> {

    private int percentPrecision;

    public PercentConverter(int percentPrecision) {
        this.percentPrecision = percentPrecision;
    }

    @Override
    public String printableValue(Percent value, boolean includeUnits) {
        double percent = value.getPercent();
        StringBuilder buffer = new StringBuilder();

        if (percentPrecision > 0) {
            buffer.append(new BigDecimal(percent, new MathContext(percentPrecision, RoundingMode.HALF_UP)));
        } else {
            buffer.append(percent);
        }

        if (includeUnits) {
            buffer.append("%");
        }

        return buffer.toString();
    }
}
