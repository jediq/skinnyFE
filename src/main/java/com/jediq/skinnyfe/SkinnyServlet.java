package com.jediq.skinnyfe;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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