package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty9.InstrumentedHandler;
import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.enricher.SkinnyErrorHandler;
import java.util.Optional;
import javax.servlet.ServletException;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class SkinnyMainServer extends SkinnyServer {

    public SkinnyMainServer(int port, Config config, MetricRegistry metrics) {
        super(port);

        try {
            HandlerCollection handlerCollection = new HandlerCollection();
            HandlerList handlerList = new HandlerList();

            if (config.getAssetsPath() != null) {
                Handler staticFileHandler = makeResourceHandler(config);
                handlerList.addHandler(instrumentHandler(staticFileHandler, metrics, "StaticFile"));
            }

            Handler skinnyFEHandler = makeServletHandler(config, metrics);
            handlerList.addHandler(instrumentHandler(skinnyFEHandler, metrics, "SkinnyFE"));

            ErrorHandler errorHandler = makeErrorHandler(config);
            server.addBean(errorHandler);
            errorHandler.setServer(server);

            handlerCollection.setHandlers(new Handler[] { handlerList });
            server.setHandler(handlerCollection);

        } catch (ServletException | NullPointerException e) {
            throw new WrappedException(e);
        }
    }

    private Handler instrumentHandler(Handler handler, MetricRegistry metrics, String name) {
        InstrumentedHandler instrumentedHandler = new InstrumentedHandler(metrics);
        instrumentedHandler.setName(name);
        instrumentedHandler.setHandler(handler);
        return instrumentedHandler;
    }

    private ErrorHandler makeErrorHandler(Config config) {
        SkinnyErrorHandler handler = new SkinnyErrorHandler(config);
        handler.setShowStacks(false);
        return handler;
    }


    private Handler makeResourceHandler(Config config) {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(config.getAssetsFolder());

        ContextHandler contextHandler = new ContextHandler(config.getAssetsPath());
        contextHandler.setHandler(resourceHandler);

        return contextHandler;
    }

    private Handler makeServletHandler(Config config, MetricRegistry metrics) throws ServletException {
        ServletHandler handler = new ServletHandler();
        ServletHolder servletHolder = new ServletHolder();
        SkinnyServlet servlet = new SkinnyServlet(config, metrics);
        servletHolder.setServlet(servlet);
        handler.addServletWithMapping(servletHolder, "/*");
        return handler;
    }
}
