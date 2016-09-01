package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty9.InstrumentedHandler;
import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.enricher.SkinnyErrorHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.ContainerLifeCycle;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class SkinnyMainServer extends SkinnyServer {

    public SkinnyMainServer(int port, Config config, MetricRegistry metrics) {
        super(port);
        HandlerCollection parentCollection = new HandlerCollection();

        Handler handler = makeSkinnyHandler(config, metrics);
        parentCollection.addHandler(instrumentHandler(handler, metrics, "SkinnyFE"));
        if (server == null) {
            throw new WrappedException("Could not start Jetty server", new NullPointerException());
        }
        server.setHandler(parentCollection);

    }

    private Handler instrumentHandler(Handler handler, MetricRegistry metrics, String name) {
        InstrumentedHandler instrumentedHandler = new InstrumentedHandler(metrics);
        instrumentedHandler.setName(name);
        instrumentedHandler.setHandler(handler);
        return instrumentedHandler;
    }

    private Handler makeSkinnyHandler(Config config, MetricRegistry metrics) {
        Handler resourceHandler = new SkinnyHandler(config, metrics);
        ContextHandler contextHandler = new ContextHandler("/");
        contextHandler.setHandler(resourceHandler);
        return contextHandler;
    }
}

