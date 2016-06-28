package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.util.Optional;

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

            HandlerCollection handlerCollection = new HandlerList();

            Optional<Handler> staticFileHandler = makeResourceHandler(config);
            staticFileHandler.ifPresent(handlerCollection::addHandler);

            Optional<Handler> skinnyFEHandler = makeServletHandler(config);
            skinnyFEHandler.ifPresent(handlerCollection::addHandler);

            server.setHandler(handlerCollection);
        } catch (ServletException | NullPointerException e) {
            throw new WrappedException(e);
        }
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
