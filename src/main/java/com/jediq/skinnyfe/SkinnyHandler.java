package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.enricher.SkinnyErrorHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import static org.apache.commons.lang3.Validate.notNull;

public class SkinnyHandler extends AbstractHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Config config;
    private final StaticHandler staticHandler;
    private final GetHandler getHandler;
    private final PostHandler postHandler;
    private final SkinnyErrorHandler errorHandler;

    public SkinnyHandler(Config config, MetricRegistry metrics) {
        this.config = config;
        this.getHandler = new GetHandler(config, metrics);
        this.postHandler = new PostHandler(config, metrics);
        this.staticHandler = makeStaticHandler(config);
        this.errorHandler = new SkinnyErrorHandler(config);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (baseRequest.isHandled()) {
            return;
        }

        if (staticHandler.handle(request, response)) {
            logger.debug("Handled by static handler");
            baseRequest.setHandled(true);
        }

        if ("GET".equals(baseRequest.getMethod()) && !baseRequest.isHandled()) {
            baseRequest.setHandled(handle(request, response));
            logger.debug("Handled by skinny get handler");
        }

        if ("POST".equals(baseRequest.getMethod()) && !baseRequest.isHandled()) {
            baseRequest.setHandled(doPost(request, response));
            logger.debug("Handled by skinny post handler");
            return;
        }

        errorHandler.handle(baseRequest, response);
    }

    private boolean handle(HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws ServletException, IOException {
        notNull(getHandler);
        com.jediq.skinnyfe.Request request = transformRequest(servletRequest);
        Response response = new ServletWrappingResponse(servletResponse);
        return getHandler.doGet(request, response);
    }

    private boolean doPost(HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws ServletException, IOException {
        notNull(postHandler);
        com.jediq.skinnyfe.Request request = transformRequest(servletRequest);
        Response response = new ServletWrappingResponse(servletResponse);

        boolean handled = postHandler.doPost(request, response);
        if (response.getStatus() > 299) {
            servletResponse.sendError(response.getStatus());
        }
        return handled;
    }

    private com.jediq.skinnyfe.Request transformRequest(HttpServletRequest servletRequest) throws MalformedURLException {
        com.jediq.skinnyfe.Request request = new com.jediq.skinnyfe.Request();
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

    private StaticHandler makeStaticHandler(Config config) {
        StaticHandler staticHandler = new StaticHandler();
        staticHandler.setPath(config.getAssetsPath());
        staticHandler.setFolder(config.getAssetsFolder());
        return staticHandler;
    }
}
