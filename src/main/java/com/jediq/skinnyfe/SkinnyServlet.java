package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

public class SkinnyServlet extends HttpServlet {

    private GetHandler getHandler;
    private PostHandler postHandler;

    @Override
    protected void doGet(HttpServletRequest servletRequest, HttpServletResponse response ) throws ServletException, IOException {
        if (getHandler == null) {
            throw new IllegalStateException("Configuration has not been set");
        }
        Request request = transformRequest(servletRequest);



        getHandler.doGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest servletRequest, HttpServletResponse response ) throws ServletException, IOException {
        if (postHandler == null) {
            throw new IllegalStateException("Configuration has not been set");
        }
        Request request = transformRequest(servletRequest);

        postHandler.doPost(request, response);
    }


    private Request transformRequest(HttpServletRequest servletRequest) throws MalformedURLException {
        Request request = new Request();
        request.url = servletRequest.getRequestURL().toString();
        String path = new URL(request.url).getPath();
        request.path = Arrays.asList(path.trim().split("/"));
        Collections.list(servletRequest.getHeaderNames())
                .forEach(name -> request.headers.put(name, servletRequest.getHeader(name)));
        if (servletRequest.getCookies() != null) {
            Arrays.stream(servletRequest.getCookies())
                    .forEach(cookie -> request.cookies.put(cookie.getName(), cookie.getValue()));
        }
        servletRequest.getParameterMap().forEach((k,v) -> request.params.put(k, v[0]));
        return request;
    }

    public void setConfig(Config config) {
        getHandler = new GetHandler(config);
        postHandler = new PostHandler(config);
    }
}