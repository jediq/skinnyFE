package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 *
 */
public class SkinnyAdminServer extends SkinnyServer {

    private final HealthCheckRegistry healthChecks = new HealthCheckRegistry();

    public SkinnyAdminServer(int port, MetricRegistry metrics) {
        super(port);

        HealthCheck adminHealthCheck = new PositiveHealthCheck();
        healthChecks.register("admin", adminHealthCheck);

        ServletHandler handler = new ServletHandler();
        handler.setServer(server);

        ServletHolder metricsServletHolder = new ServletHolder(new MetricsServlet(metrics));
        handler.addServletWithMapping(metricsServletHolder, "/metrics");

        ServletHolder healthCheckServletHolder = new ServletHolder(new HealthCheckServlet(healthChecks));
        handler.addServletWithMapping(healthCheckServletHolder,  "/healthcheck");

        server.setHandler(handler);
    }

    private class PositiveHealthCheck extends HealthCheck {
        @Override
        protected Result check() throws Exception {
            return Result.healthy();
        }
    }
}
