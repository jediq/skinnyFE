package com.jediq.skinnyfe;

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

    public static final String WRAPPED_EXCEPTION_MESSAGE = "Caught exception starting SkinnyFE server on port : ";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public SkinnyMainServer(int port, Config config) {
        super(port);

        try {
            HandlerCollection handlerCollection = new HandlerCollection();
            HandlerList handlerList = new HandlerList();

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
}
