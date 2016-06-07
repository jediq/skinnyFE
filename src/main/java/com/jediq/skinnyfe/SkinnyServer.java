package com.jediq.skinnyfe;

import javax.servlet.ServletException;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 *
 */
public class SkinnyServer {

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    private int port;
    private Server server;

    public SkinnyServer(int port, Config config) {
        try {
            this.server = constructServer(port);
            this.port = port;

            HandlerCollection handlerCollection = new HandlerCollection();

            Optional<Handler> resourceHandler = makeResourceHandler(config);
            resourceHandler.ifPresent(handlerCollection::addHandler);

            Optional<Handler> servletHandler = makeServletHandler(config);
            servletHandler.ifPresent(handlerCollection::addHandler);

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
            logger.info("Caught exception starting SkinnyFE server on port : " + port);
            throw new WrappedException(e);
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            logger.info("Caught exception stopping SkinnyFE server on port : " + port);
            throw new WrappedException(e);
        }
    }
}
