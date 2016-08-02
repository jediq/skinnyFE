package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkinnyFE {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
            skinnyFE.startMainServer(skinnyFE.getConfig().getPort());

            if (skinnyFE.getConfig().getAdminPort() > 0) {
                skinnyFE.startAdminServer(skinnyFE.getConfig().getAdminPort());
            }
        }
    }

    private static void printUsage() {
        StringBuilder builder = new StringBuilder();
        builder.append("Usage:\n");
        builder.append("java -jar skinnyFE-all.jar config.json\n");
        builder.append("   config.json - Your config file\n");
        System.out.println(builder.toString()); //NOSONAR
    }

    public void startMainServer(int port) {
        logger.debug("Starting SkinnyFE server on port : " + port);
        SkinnyMainServer server = new SkinnyMainServer(port, config);
        server.start();
        servers.put(port, server);
        logger.info("Started SkinnyFE server for " + config.getName() + " on port : " + port);
    }

    public void startAdminServer(int port) {
        logger.debug("Starting SkinnyFE server on port : " + port);
        SkinnyAdminServer server = new SkinnyAdminServer(port, config);
        server.start();
        servers.put(port, server);
        logger.info("Started SkinnyFE server for " + config.getName() + " on port : " + port);
    }

    public void stopServer(int port) {
        logger.debug("Stopping SkinnyFE server on port : " + port);
        servers.get(port).stop();
        logger.info("Stopped SkinnyFE server on port : " + port);
    }

    public Config getConfig() {
        return config;
    }

}
