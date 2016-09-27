package io.bootique.metrics.web;

import com.codahale.metrics.health.HealthCheck;
import io.bootique.metrics.healthcheck.HealthCheckRegistry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @since 0.8
 */
// inspired com.yammer.metrics.servlet.HealthCheckServlet, only better integrated to Bootique.
// TODO: verbosity levels .. perhaps use nagios plugin format?
public class HealthCheckServlet extends HttpServlet {

    private static final String CONTENT_TYPE = "text/plain";
    private HealthCheckRegistry registry;

    public HealthCheckServlet(HealthCheckRegistry registry) {
        this.registry = registry;
    }

    private static boolean isAllHealthy(Map<String, HealthCheck.Result> results) {
        for (HealthCheck.Result result : results.values()) {
            if (!result.isHealthy()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (PrintWriter writer = response.getWriter()) {
            doWrite(response, writer);
        }
    }

    protected void doWrite(HttpServletResponse response, PrintWriter writer) throws IOException {

        Map<String, HealthCheck.Result> results = registry.runHealthChecks();

        response.setContentType(CONTENT_TYPE);
        response.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");

        if (results.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            writer.println("! No health checks registered.");
            return;
        }

        if (isAllHealthy(results)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        for (Map.Entry<String, HealthCheck.Result> entry : results.entrySet()) {

            HealthCheck.Result result = entry.getValue();
            if (result.isHealthy()) {
                if (result.getMessage() != null) {
                    writer.format("* %s: OK - %s\n", entry.getKey(), result.getMessage());
                } else {
                    writer.format("* %s: OK\n", entry.getKey());
                }
            } else {
                if (result.getMessage() != null) {
                    writer.format("! %s: ERROR - %s\n", entry.getKey(), result.getMessage());
                }

                @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
                Throwable error = result.getError();
                if (error != null) {
                    writer.println();
                    error.printStackTrace(writer);
                    writer.println();
                }
            }
        }
    }
}
