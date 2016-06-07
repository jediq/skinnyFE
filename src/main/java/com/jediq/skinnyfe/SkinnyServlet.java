package com.jediq.skinnyfe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.JsonNodeValueResolver;
import com.github.jknack.handlebars.Template;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkinnyServlet extends HttpServlet {

    private GetHandler getHandler;
    private PostHandler postHandler;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        if (getHandler == null) {
            throw new IllegalStateException("Configuration has not been set");
        }
        getHandler.doGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        if (postHandler == null) {
            throw new IllegalStateException("Configuration has not been set");
        }
        postHandler.doPost(request, response);
    }



    public void setConfig(Config config) {
        getHandler = new GetHandler(config);
        postHandler = new PostHandler(config);
    }
}