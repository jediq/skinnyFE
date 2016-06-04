package com.jediq.skinnyfe;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkinnyFE {

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<Integer, SkinnyServer> servers;
    private final TemplateResolver templateResolver;
    private final ResourceLoader resourceLoader;
    private final Config config;

    public SkinnyFE(String configLocation) {
        this.servers = new HashMap<>();
        this.config = Config.load(configLocation);
        this.templateResolver = new TemplateResolver(config.getTemplates());
        this.resourceLoader = new ResourceLoader(config);
    }

    public static void main(String[] args ) {
        String configLocation = args.length == 1 ? args[0] :  "src/test/resources/basic/config.json";
        SkinnyFE skinnyFE = new SkinnyFE(configLocation);
        skinnyFE.startServer(skinnyFE.getConfig().getPort());
    }

    public void startServer(int port) {
        logger.info("Starting SkinnyFE server on port : " + port);
        SkinnyServer server = new SkinnyServer(port, templateResolver, resourceLoader);
        server.start();
        servers.put(port, server);
        logger.info("Started SkinnyFE server on port : " + port);
    }

    public void stopServer(int port) {
        logger.info("Stopping SkinnyFE server on port : " + port);
        servers.get(port).stop();
        logger.info("Stopped SkinnyFE server on port : " + port);
    }

    public Config getConfig() {
        return config;
    }
}
