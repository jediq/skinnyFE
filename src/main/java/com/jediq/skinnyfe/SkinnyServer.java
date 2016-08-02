package com.jediq.skinnyfe;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class SkinnyServer {

    protected static final String WRAPPED_EXCEPTION_MESSAGE = "Caught exception starting %s server on port : %s";
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected int port;
    protected Server server;

    public SkinnyServer(int port) {
        this.server = constructServer(port);
        this.port = port;
    }

    protected Server constructServer(int port) {
        return new Server(port);
    }

    public void start() {
        try {
            server.start();
        } catch (Exception e) {
            logger.info(getErrorMessage());
            throw new WrappedException(getErrorMessage(), e);
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            logger.info(getErrorMessage());
            throw new WrappedException(getErrorMessage(), e);
        }
    }

    protected String getErrorMessage() {
        return String.format(WRAPPED_EXCEPTION_MESSAGE, getClass().getSimpleName(), port);
    }
}
