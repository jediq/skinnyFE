package com.jediq.skinnyfe;


import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class FixedResponseJetty implements Closeable {

    private int port;
    private Server server;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LinkedList<FixedResponse> responses = new LinkedList<>();

    public FixedResponseJetty() {
        this(new Random().nextInt(8900)+1100);
    }

    public FixedResponseJetty(int port) {
        server = new Server(port);
        server.setHandler(new AbstractHandler() {
            public void handle(String target, Request baseRequest, HttpServletRequest request,
                               HttpServletResponse response) throws IOException, ServletException {
                respond(request, response);
                baseRequest.setHandled(true);
            }
        });

        logger.info("Configured Jetty on port : {}", port);
    }

    protected void respond(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (responses.isEmpty()) {
            response.setStatus(404);
            return;
        }
        logger.info("Returning response for : {}", request.toString());
        FixedResponse nextResponse = responses.removeFirst();
        response.setStatus(200);
        response.setContentType(nextResponse.contentType);
        response.getWriter().write(nextResponse.value);
    }

    public void start() {
        try {
            server.start();
            logger.info("Started Jetty on port : {}", port);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addResponseString(String responseString, String contentType) {
        FixedResponse response = new FixedResponse();
        response.value = responseString;
        response.contentType = contentType;
        this.responses.add(response);
    }

    public int getPort() {
        return port;
    }

    @Override
    public void close() throws IOException {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class FixedResponse {
        String value;
        String contentType;
    }
}
