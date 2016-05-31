package com.jediq.skinnyfe;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class SkinnyFE {


    private List<Resource> resources;
    private String templatesLocation;

    public static void main(String[] args ) throws Exception {
        String resourcesFile = args[0];
        String templatesLocation = args[1];
        new SkinnyFE(resourcesFile, templatesLocation);
    }

    public SkinnyFE(String resourcesFile, String templatesLocation) throws Exception {
        this.templatesLocation = templatesLocation;

        loadResources(resourcesFile);
        startServer();
    }

    private void loadResources(String resourcesFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        
    }

    private void startServer() throws Exception {
        Server server = new Server(8008);
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        handler.addServletWithMapping(HelloServlet.class, "/*");
        server.start();
        server.join();
    }

    public static class HelloServlet extends HttpServlet {
        @Override
        protected void doGet( HttpServletRequest request,
                              HttpServletResponse response ) throws ServletException, IOException {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<h1>Hello from HelloServlet</h1>");
        }
    }
}
