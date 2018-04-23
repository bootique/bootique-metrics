package io.bootique.metrics;

import com.google.inject.Module;
import io.bootique.names.ClassToName;

/**
 * A helper class to name metrics across Bootique modules. Helps somewhat to enforce consistent naming convention. Note that
 *
 * @since 0.26
 */
public class MetricNaming {

    protected static ClassToName MODULE_NAME_BUILDER = ClassToName
            .builder()
            .stripSuffix("Module")
            .stripSuffix("Instrumented")
            .build();

    private String prefix;

    protected MetricNaming(String prefix) {
        this.prefix = prefix;
    }

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

    /**
     * Creates a metrics naming builder for a root module class. Note that to generate a proper name, module class
     * should follow a naming convention of "XyzModule" or "XyzInstrumentedModule".
     *
     * @param metricSourceModule a type of module where a given set of metrics originates.
     * @return a {@link MetricNaming} name builder for a specific module.
     */
    public static MetricNaming forModule(Class<? extends Module> metricSourceModule) {
        return new MetricNaming("bq." + MODULE_NAME_BUILDER.toName(metricSourceModule));
    }

    static String fromParts(String... parts) {
        return String.join(".", parts);
    }

    public String name(String metricType, String metricName) {
        return fromParts(prefix, metricType, metricName);
    }

    public String name(
            String metricType,
            String metricInstanceName,
            String metricName) {
        return fromParts(prefix, metricType, metricInstanceName, metricName);
    }
}
