package com.jediq.skinnyfe;

import javax.servlet.ServletException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class SkinnyServer {

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    private int port;
    private Server server;

    public SkinnyServer(int port, TemplateResolver templateResolver, ResourceLoader resourceLoader) {
        this.server = new Server(port);
        this.port = port;

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        ServletHolder servletHolder = handler.addServletWithMapping(SkinnyServlet.class, "/*");
        try {
            SkinnyServlet servlet = (SkinnyServlet) servletHolder.getServlet();
            servlet.setTemplateResolver(templateResolver);
            servlet.setResourceLoader(resourceLoader);
        } catch (ServletException e) {
            throw new WrappedException(e);
        }
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