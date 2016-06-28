package com.jediq.skinnyfe.config;

import com.jediq.skinnyfe.HandlebarsCompiler;
import com.jediq.skinnyfe.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Resource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String name;
    private String url;
    private String methods = "GET";
    private Map<String, String> headers = new HashMap<>();

    private HandlebarsCompiler handlebarsCompiler = new HandlebarsCompiler();

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
        logger.debug("Resolving URL : {}", getUrl());
        // need to iterate twice to resolve all placeholders
        String resolved = handlebarsCompiler.compile(getUrl(), enrichmentValues, 2);
        logger.debug("Resolved URL to : {}", resolved);
        return resolved;
    }
}
