package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
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

            Connector connector = new LocalConnector(server);
            server.addConnector(connector);

            HandlerCollection handlerCollection = new HandlerCollection();
            HandlerList handlerList = new HandlerList();

            Optional<Handler> staticFileHandler = makeResourceHandler(config);
            staticFileHandler.ifPresent(handlerList::addHandler);

            Optional<Handler> skinnyFEHandler = makeServletHandler(config);
            skinnyFEHandler.ifPresent(handlerList::addHandler);

            Optional<ErrorPageErrorHandler> errorHandler = makeErrorHandler(config);
            errorHandler.ifPresent(handlerList::addHandler);

            handlerCollection.setHandlers(new Handler[] { handlerList });

            server.setHandler(handlerCollection);

        } catch (ServletException | NullPointerException e) {
            throw new WrappedException(e);
        }
    }

    private Optional<ErrorPageErrorHandler> makeErrorHandler(Config config) {
        if (config.getErrorPages().isEmpty()) {
            return Optional.empty();
        }
        ErrorPageErrorHandler handler = new MyErrorPageErrorHandler();
        for (Map.Entry<Integer, String> page : config.getErrorPages().entrySet()) {
            handler.addErrorPage(page.getKey(), page.getValue());
        }
        return Optional.of(handler);
    }

    private class MyErrorPageErrorHandler extends ErrorPageErrorHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
            super.handle(target, baseRequest, request, response);
        }

        @Override
        protected void handleErrorPage(HttpServletRequest request, Writer writer, int code, String message) throws IOException {
            super.handleErrorPage(request, writer, code, message);
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
