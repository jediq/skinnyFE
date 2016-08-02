package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.jediq.skinnyfe.config.Config;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkinnyFE {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MetricRegistry metrics = new MetricRegistry();
    private final Map<Integer, SkinnyServer> servers;
    private final Config config;

    public SkinnyFE(String configLocation) {
        this.servers = new HashMap<>();
        this.config = Config.load(configLocation);
    }

    public static void main(String[] args ) {
        if (args.length != 1) {
            printUsage();
            throw new IllegalArgumentException("No config file found");
        } else {
            String configLocation = args[0];
            SkinnyFE skinnyFE = new SkinnyFE(configLocation);
            skinnyFE.start();
        }
    }

    private static void printUsage() {
        StringBuilder builder = new StringBuilder();
        builder.append("Usage:\n");
        builder.append("java -jar skinnyFE-all.jar config.json\n");
        builder.append("   config.json - Your config file\n");
        System.out.println(builder.toString()); //NOSONAR
    }

    public void start() {
        startMainServer(getConfig().getPort());

        if (getConfig().getAdminPort() > 0) {
            startAdminServer(getConfig().getAdminPort());
        }
    }

    public void startMainServer(int port) {
        logger.debug("Starting SkinnyFE server on port : " + port);
        SkinnyMainServer server = new SkinnyMainServer(port, config, metrics);
        server.start();
        servers.put(port, server);
        logger.info("Started SkinnyFE server for " + config.getName() + " on port : " + port);
    }

    public void startAdminServer(int port) {
        logger.debug("Starting SkinnyFE admin server on port : " + port);
        SkinnyAdminServer server = new SkinnyAdminServer(port, metrics);
        server.start();
        servers.put(port, server);
        logger.info("Started SkinnyFE admin server for " + config.getName() + " on port : " + port);
    }

    public void stopServer(int port) {
        logger.debug("Stopping SkinnyFE server on port : " + port);
        servers.get(port).stop();
        logger.info("Stopped SkinnyFE server on port : " + port);
    }

    public void stopServers() {
        servers.keySet().forEach(this::stopServer);
    }

    public Config getConfig() {
        return config;
    }

}
