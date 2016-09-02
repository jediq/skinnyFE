package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty9.InstrumentedHandler;
import com.jediq.skinnyfe.config.Config;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.*;

/**
 *
 */
public class SkinnyMainServer extends SkinnyServer {

    private final HandlerCollection handlers;
    private final MetricRegistry metrics;

    public SkinnyMainServer(int port, MetricRegistry metrics) {
        super(port);
        this.metrics = metrics;

        if (server == null) {
            throw new WrappedException("Could not start Jetty server", new NullPointerException());
        }

        // make a new mutable handler collection so we can add at any time.
        this.handlers = new HandlerCollection(true);
        server.setHandler(handlers);
    }

    public void addConfiguration(Config config) {
        Handler handler = makeSkinnyHandler(config, metrics);
        handlers.addHandler(instrumentHandler(handler, metrics, "SkinnyFE-" + config.getName()));
    }

    private Handler instrumentHandler(Handler handler, MetricRegistry metrics, String name) {
        InstrumentedHandler instrumentedHandler = new InstrumentedHandler(metrics);
        instrumentedHandler.setName(name);
        instrumentedHandler.setHandler(handler);
        return instrumentedHandler;
    }

    private Handler makeSkinnyHandler(Config config, MetricRegistry metrics) {
        Handler skinnyHandler = new SkinnyHandler(config, metrics);
        Handler instrumented = instrumentHandler(skinnyHandler, metrics, "SkinnyFE-" + config.getName());
        ContextHandler contextHandler = new ContextHandler("/");
        contextHandler.setHandler(instrumented);
        if (config.getVirtualHost() != null) {
            contextHandler.setVirtualHosts(new String [] { config.getVirtualHost() } );
        }
        return contextHandler;
    }
}

