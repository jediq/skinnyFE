package com.jediq.skinnyfe;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.eclipse.jetty.servlet.ServletHolder;

public class SkinnyFE {


    private List<Resource> resources;
    private String templatesLocation;

    TemplateResolver templateResolver;

    public static void main(String[] args ) throws IOException {
        if (args.length != 2) {
            args = new String[] {
                    "src/test/resources/basic/config.json",
                    "src/test/resources/basic/templates"
            };
        }
        String configLocation = args[0];
        String templatesLocation = args[1];
        new SkinnyFE(configLocation, templatesLocation);
    }

    public SkinnyFE(String configLocation, String templatesLocation) throws IOException{
        this.templatesLocation = templatesLocation;
        this.templateResolver = new TemplateResolver(templatesLocation);

        loadConfig(configLocation);
        startServer();
    }

    private void loadConfig(String configLocation) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Config config = objectMapper.readValue(new File(configLocation), Config.class);
        this.resources = config.getResources();
    }

    private void startServer() throws IOException {
        Server server = new Server(8008);
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        ServletHolder servletHolder = handler.addServletWithMapping(SkinnyServlet.class, "/*");
        try {
            ((SkinnyServlet) servletHolder.getServlet()).setTemplateResolver(templateResolver);

            server.start();
            server.join();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}
