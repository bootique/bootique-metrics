package io.bootique.metrics;

import com.google.inject.Module;
import io.bootique.names.ClassToName;

import java.util.StringJoiner;

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

    private String modulePrefix;

    protected MetricNaming(String modulePrefix) {
        this.modulePrefix = modulePrefix;
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

    public String name(String... names) {

        StringJoiner joiner = new StringJoiner(".").add(modulePrefix);

        for (String name : names) {
            joiner.add(name);
        }

        return joiner.toString();
    }
}
