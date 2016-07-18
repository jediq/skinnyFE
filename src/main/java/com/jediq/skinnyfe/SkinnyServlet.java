package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.apache.commons.lang3.Validate.notNull;

public class SkinnyServlet extends HttpServlet {

    private transient GetHandler getHandler;
    private transient PostHandler postHandler;

    @Override
    protected void doGet(HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws ServletException, IOException {
        notNull(getHandler);
        Request request = transformRequest(servletRequest);
        Response response = new ServletWrappingResponse(servletResponse);

        getHandler.doGet(request, response);
        if (response.getStatus() > 299) {

            servletResponse.sendError(response.getStatus());
        }
    }

    @Override
    protected void doPost(HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws ServletException, IOException {
        notNull(postHandler);
        Request request = transformRequest(servletRequest);
        Response response = new ServletWrappingResponse(servletResponse);

        postHandler.doPost(request, response);
    }

    private Request transformRequest(HttpServletRequest servletRequest) throws MalformedURLException {
        Request request = new Request();
        request.setUrl(servletRequest.getRequestURL().toString());
        String path = new URL(request.getUrl()).getPath();
        request.setPath(Arrays.asList(path.trim().split("/")));
        Collections.list(servletRequest.getHeaderNames())
                .forEach(name -> request.getHeaders().put(name, servletRequest.getHeader(name)));
        if (servletRequest.getCookies() != null) {
            Arrays.stream(servletRequest.getCookies())
                    .forEach(cookie -> request.getCookies().put(cookie.getName(), cookie.getValue()));
        }
        servletRequest.getParameterMap().forEach((k,v) -> request.getParams().put(k, v[0]));
        return request;
    }

    public void setConfig(Config config) {
        getHandler = new GetHandler(config);
        postHandler = new PostHandler(config);
    }
}