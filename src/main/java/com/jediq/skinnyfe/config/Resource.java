package com.jediq.skinnyfe.config;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.jediq.skinnyfe.Request;
import com.jediq.skinnyfe.WrappedException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Resource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String name;
    private String url;
    private String methods = "GET";
    private String enricher;
    private Map<String, String> headers = new HashMap<>();

    private Handlebars handlebars = new Handlebars();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethods() {
        return methods;
    }

    public void setMethods(String methods) {
        this.methods = methods;
    }

    public String getEnricher() {
        return enricher;
    }

    public void setEnricher(String enricher) {
        this.enricher = enricher;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getResolvedUrl(String identifier, Request request) {
        Map <String, Object> map = new HashMap<>();
        map.put("identifier", identifier);
        map.put("URL", request.getUrl());
        map.put("COOKIE", request.getCookies());
        map.put("HEADER", request.getHeaders());
        map.put("PATH", request.getPath());
        map.put("PARAM", request.getParams());
        return getResolvedUrl(map);
    }

    public String getResolvedUrl(Map<String, Object> enrichmentValues) {
        try {
            logger.debug("Resolving URL : {}", getUrl());
            Template template = handlebars.compileInline(getUrl());
            String firstPass = template.apply(enrichmentValues);

            // Need to do this twice to allow for identifiers that need resolving again
            Template secondTemplate = handlebars.compileInline(firstPass);
            String resolved = secondTemplate.apply(enrichmentValues);
            logger.debug("Resolved URL to : {}", resolved);
            return resolved;
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }
}
