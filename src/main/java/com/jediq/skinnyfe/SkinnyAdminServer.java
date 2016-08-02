package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.AdminServlet;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import com.jediq.skinnyfe.config.Config;
import java.util.EventListener;
import java.util.Optional;
import javax.servlet.ServletException;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.BaseHolder;
import org.eclipse.jetty.servlet.ListenerHolder;
import org.eclipse.jetty.servlet.ServletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class SkinnyAdminServer extends SkinnyServer {

    public static final String WRAPPED_EXCEPTION_MESSAGE = "Caught exception starting SkinnyFE Admin server on port : ";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SkinnyAdminServer(int port, Config config) {
        super(port);
    }

    private Optional<Handler> makeMetricsHandler(Server server, Config config) throws ServletException {
        ServletHandler handler = new ServletHandler();
        handler.setServer(server);

        MetricRegistry metrics = new MetricRegistry();
        HealthCheckRegistry healthChecks = new HealthCheckRegistry();

        handler.getServletContext().setAttribute(MetricsServlet.METRICS_REGISTRY, metrics);
        handler.getServletContext().setAttribute(HealthCheckServlet.HEALTH_CHECK_REGISTRY, healthChecks);

        handler.addServletWithMapping(AdminServlet.class, "/_metrics");


        return Optional.of(handler);
    }

    private ListenerHolder wrapListener(EventListener listener) {
        ListenerHolder listenerHolder = new ListenerHolder(BaseHolder.Source.EMBEDDED);
        listenerHolder.setListener(listener);
        return listenerHolder;
    }
}
