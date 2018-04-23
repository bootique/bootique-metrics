package io.bootique.metrics;

import com.google.inject.Module;
import io.bootique.names.ClassToName;

/**
 * A helper class to name metrics across Bootique modules. Helps somewhat to enforce consistent naming convention.
 *
 * @since 0.26
 */
public class MetricNaming {

    protected static ClassToName MODULE_NAME_BUILDER = ClassToName
            .builder()
            .stripSuffix("Module")
            .stripSuffix("Instrumented")
            .build();


    public static String name(Class<? extends Module> metricSourceModule, String metricType, String metricName) {
        return fromParts("bq", MODULE_NAME_BUILDER.toName(metricSourceModule), metricType, metricName);
    }

    public static String name(
            Class<? extends Module> metricSourceModule,
            String metricType,
            String metricInstanceName,
            String metricName) {
        return fromParts("bq", MODULE_NAME_BUILDER.toName(metricSourceModule), metricType, metricInstanceName, metricName);
    }

    static String fromParts(String... parts) {
        return String.join(".", parts);
    }
}
