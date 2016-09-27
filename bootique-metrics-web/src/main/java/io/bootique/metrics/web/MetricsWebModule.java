package io.bootique.metrics.web;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedServlet;
import io.bootique.metrics.health.HealthCheckRegistry;

import java.util.Collections;

/**
 * @since 0.8
 */
public class MetricsWebModule implements Module {

    @Override
    public void configure(Binder binder) {
        JettyModule.contributeMappedServlets(binder).addBinding().to(new TypeLiteral<MappedServlet<HealthCheckServlet>>() {
        });
    }

    @Singleton
    @Provides
    MappedServlet<HealthCheckServlet> provideHealthCheckServlet(HealthCheckRegistry registry) {
        HealthCheckServlet servlet = new HealthCheckServlet(registry);
        return new MappedServlet<HealthCheckServlet>(servlet, Collections.singleton("/health"), "health");
    }
}
