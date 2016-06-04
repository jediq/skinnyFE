package com.jediq.skinnyfe;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
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
    private final Map<Integer, Server> servers;
    private final TemplateResolver templateResolver;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final Config config;

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
        this.objectMapper = new ObjectMapper();
        this.servers = new HashMap<>();
        this.config = loadConfig(configLocation);
        this.resourceLoader = new ResourceLoader(config);
    }

    private Config loadConfig(String configLocation) {
        try {
            return objectMapper.readValue(new File(configLocation), Config.class);
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    public void startServer(int port) {
        logger.info("Starting SkinnyFE server on port : " + port);
        Server server = new Server(port);
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        ServletHolder servletHolder = handler.addServletWithMapping(SkinnyServlet.class, "/*");
        try {
            SkinnyServlet servlet = (SkinnyServlet) servletHolder.getServlet();
            servlet.setTemplateResolver(templateResolver);
            servlet.setResourceLoader(resourceLoader);

            server.start();
            servers.put(port, server);
            logger.info("Started SkinnyFE server on port : " + port);
        } catch (Exception e) {
            logger.info("Caught exception starting SkinnyFE server on port : " + port);
            throw new WrappedException(e);
        }
    }

    public void stopServer(int port) {
        try {
            logger.info("Starting SkinnyFE server on port : " + port);
            servers.get(port).stop();
            logger.info("Stopped SkinnyFE server on port : " + port);
        } catch (Exception e) {
            logger.info("Caught exception stopping SkinnyFE server on port : " + port);
            throw new WrappedException(e);
        }
    }
}
