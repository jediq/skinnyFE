package com.jediq.skinnyfe;

import java.io.File;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import java.io.IOException;
import java.util.List;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkinnyFE {

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<Resource> resources;
    private Server server;
    private TemplateResolver templateResolver;
    private ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args ) {
        if (args.length != 2) {
            args = new String[] {
                    "src/test/resources/basic/config.json",
                    "src/test/resources/basic/templates"
            };
        }
        String configLocation = args[0];
        String templatesLocation = args[1];
        new SkinnyFE(configLocation, templatesLocation).startServer(8008);
    }

    public SkinnyFE(String configLocation, String templatesLocation) {
        this.templateResolver = new TemplateResolver(templatesLocation);

        loadConfig(configLocation);
    }

    private void loadConfig(String configLocation) {
        try {
            Config config = objectMapper.readValue(new File(configLocation), Config.class);
            this.resources = config.getResources();
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    public void startServer(int port) {
        logger.info("Starting SkinnyFE server on port : " + port);
        server = new Server(port);
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        ServletHolder servletHolder = handler.addServletWithMapping(SkinnyServlet.class, "/*");
        try {
            ((SkinnyServlet) servletHolder.getServlet()).setTemplateResolver(templateResolver);

            server.start();
            logger.info("Started SkinnyFE server on port : " + port);
        } catch (Exception e) {
            logger.info("Caught exception starting SkinnyFE server on port : " + port);
            throw new WrappedException(e);
        }
    }

    public void stopServer(int port) {
        try {
            logger.info("Starting SkinnyFE server on port : " + port);
            server.stop();
            logger.info("Stopped SkinnyFE server on port : " + port);
        } catch (Exception e) {

            logger.info("Caught exception stopping SkinnyFE server on port : " + port);
            throw new WrappedException(e);
        }
    }
}
