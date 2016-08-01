package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty9.InstrumentedHandler;
import com.codahale.metrics.servlets.MetricsServlet;
import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.enricher.SkinnyErrorHandler;
import java.util.Optional;
import javax.servlet.ServletException;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class SkinnyServer {

    public static final String WRAPPED_EXCEPTION_MESSAGE = "Caught exception starting SkinnyFE server on port : ";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private int port;
    private Server server;

    public SkinnyServer(int port, Config config) {
        try {
            this.server = constructServer(port);
            this.port = port;

            HandlerCollection handlerCollection = new HandlerCollection();
            HandlerList handlerList = new HandlerList();

            MetricRegistry registry = new MetricRegistry();
            InstrumentedHandler handler = new InstrumentedHandler(registry, "SkinnyFE-registry");
            handlerList.addHandler(handler);

            ServletHandler servletHandler = new ServletHandler();
            servletHandler.addServletWithMapping(MetricsServlet.class, "/metrics");
            handlerList.addHandler(new InstrumentedHandler(registry, "SkinnyFE-servlet"));

            Optional<Handler> staticFileHandler = makeResourceHandler(config);
            staticFileHandler.ifPresent(handlerList::addHandler);

            Optional<Handler> skinnyFEHandler = makeServletHandler(config);
            skinnyFEHandler.ifPresent(handlerList::addHandler);

            ErrorHandler errorHandler = makeErrorHandler(config);
            server.addBean(errorHandler);

            handlerCollection.setHandlers(new Handler[] { handlerList });

            server.setHandler(handlerCollection);

        } catch (ServletException | NullPointerException e) {
            throw new WrappedException(e);
        }
    }

    private ErrorHandler makeErrorHandler(Config config) {
        SkinnyErrorHandler handler = new SkinnyErrorHandler(config);
        handler.setShowStacks(false);

        return handler;
    }


    private Optional<Handler> makeResourceHandler(Config config) {
        if (config.getAssetsPath() == null) {
            return Optional.empty();
        }

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(config.getAssetsFolder());

        ContextHandler contextHandler = new ContextHandler(config.getAssetsPath());
        contextHandler.setHandler(resourceHandler);

        return Optional.of(contextHandler);
    }

    private Optional<Handler> makeServletHandler(Config config) throws ServletException {
        ServletHandler handler = new ServletHandler();
        ServletHolder servletHolder = handler.addServletWithMapping(SkinnyServlet.class, "/*");
        SkinnyServlet servlet = (SkinnyServlet) servletHolder.getServlet();
        servlet.setConfig(config);
        return Optional.of(handler);
    }

    protected Server constructServer(int port) {
        return new Server(port);
    }


    public void start() {
        try {
            server.start();
        } catch (Exception e) {
            logger.info(WRAPPED_EXCEPTION_MESSAGE + port);
            throw new WrappedException(WRAPPED_EXCEPTION_MESSAGE + port, e);
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            logger.info(WRAPPED_EXCEPTION_MESSAGE + port);
            throw new WrappedException(WRAPPED_EXCEPTION_MESSAGE + port, e);
        }
    }
}
