package io.bootique.metrics.health.check;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.StringTokenizer;

/**
 * A value object that defines a range of int values, mapping health check statuses to different parts of the range.
 * Can be used as a value object in YAML/JSON.
 *
 * @since 0.25
 */
public class IntRange extends ValueRange<Integer> {

    protected IntRange(Integer warningThreshold, Integer criticalThreshold) {
        super(warningThreshold, criticalThreshold);
    }

    /**
     * Creates an {@link IntRange} from a comma-separated String.
     *
     * @param encoded a comma-separated String that defines health check thresholds for the range. Can be either
     *                "", "int" or "int, int", specifying no thresholds, a critical threshold, warning and critical
     *                thresholds.
     * @return a non-null {@link IntRange} corresponding to the encoded String.
     */
    @JsonCreator
    public static IntRange parse(String encoded) {

        if (encoded == null) {
            return new IntRange(null, null);
        }

        // comma-separated format; spaces are insignificant
        StringTokenizer tokens = new StringTokenizer(encoded, ",");

        switch (tokens.countTokens()) {
            case 0:
                return new IntRange(null, null);
            case 1:
                return new IntRange(null, fromString(encoded, tokens.nextToken()));
            case 2:
                return new IntRange(
                        fromString(encoded, tokens.nextToken()),
                        fromString(encoded, tokens.nextToken()));
            default:
                throw new RuntimeException("Invalid threshold String '" + encoded
                        + "'. Only two components are expected - warning threshold and critical threshold.");
        }
    }

    private static int fromString(String fullString, String string) {
        try {
            return Integer.parseInt(string.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Can't parse to int the '" + string + "' part of the threshold String '" +
                    fullString + "'.");
        }
    }
}
