package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;

/**
 *
 */
public class SkinnyMetricsServlet extends MetricsServlet {


    public static final HealthCheckRegistry HEALTH_CHECK_REGISTRY = new HealthCheckRegistry();
    public static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    public static class MetricsContextListener extends MetricsServlet.ContextListener {
        @Override
        protected MetricRegistry getMetricRegistry() {
            return METRIC_REGISTRY;
        }
    }

    public static class HealthCheckContextListener extends HealthCheckServlet.ContextListener {
        @Override
        protected HealthCheckRegistry getHealthCheckRegistry() {
            return HEALTH_CHECK_REGISTRY;
        }
    }
}
