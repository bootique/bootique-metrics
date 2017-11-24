package io.bootique.metrics.health.check;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.StringTokenizer;

/**
 * A value object that defines a range of double values, mapping health check statuses to different parts of the range.
 * Can be used as a value object in YAML/JSON.
 *
 * @since 0.25
 */
public class DoubleRange extends ValueRange<Double> {

    protected DoubleRange(Double warningThreshold, Double criticalThreshold) {
        super(warningThreshold, criticalThreshold);
    }

    /**
     * Creates an {@link DoubleRange} from a comma-separated String.
     *
     * @param encoded a comma-separated String that defines health check thresholds for the range. Can be either
     *                "", "double" or "double, double", specifying no thresholds, a critical threshold, warning and critical
     *                thresholds.
     * @return a non-null {@link Double} corresponding to the encoded String.
     */
    @JsonCreator
    public static DoubleRange parse(String encoded) {

        if (encoded == null) {
            return new DoubleRange(null, null);
        }

        // comma-separated format; spaces are insignificant
        StringTokenizer tokens = new StringTokenizer(encoded, ",");

        switch (tokens.countTokens()) {
            case 0:
                return new DoubleRange(null, null);
            case 1:
                return new DoubleRange(null, fromString(encoded, tokens.nextToken()));
            case 2:
                return new DoubleRange(
                        fromString(encoded, tokens.nextToken()),
                        fromString(encoded, tokens.nextToken()));
            default:
                throw new RuntimeException("Invalid threshold String '" + encoded
                        + "'. Only two components are expected - warning threshold and critical threshold.");
        }
    }

    private static double fromString(String fullString, String string) {
        try {
            return Double.parseDouble(string.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Can't parse to double the '" + string + "' part of the threshold String '" +
                    fullString + "'.");
        }
    }
}
