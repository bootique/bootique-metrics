package io.bootique.metrics.health.writer;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.value.Duration;
import io.bootique.value.Percent;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 1.0.RC1
 */
@BQConfig
@JsonTypeName("nagios")
public class NagiosReportWriterFactory implements ReportWriterFactory {

    private int percentPrecision;

    public NagiosReportWriterFactory() {
        percentPrecision = 4;
    }

    @Override
    public ReportWriter createReportWriter() {
        return new NagiosReportWriter(createConverters());
    }

    private Map<Class<?>, ValueConverter<?>> createConverters() {
        Map<Class<?>, ValueConverter<?>> converters = new HashMap<>();
        converters.put(Percent.class, new PercentConverter(percentPrecision));
        converters.put(Duration.class, new DurationConverter());
        return converters;
    }

    @BQConfigProperty("Defines precision of percent values in the report. The default is 4.")
    public void setPercentPrecision(int percentPrecision) {
        this.percentPrecision = percentPrecision;
    }
}
