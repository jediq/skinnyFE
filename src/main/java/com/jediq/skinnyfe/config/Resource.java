package com.jediq.skinnyfe.config;

import com.jediq.skinnyfe.HandlebarsCompiler;
import com.jediq.skinnyfe.Request;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Resource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String name;
    private String url;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> inputValidators = new HashMap<>();

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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getInputValidators() {
        return inputValidators;
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

    public void validateInput(String key, String value) {
        String regex = getInputValidators().getOrDefault(key, ".*");
        logger.debug("Validating {} against {}", key, regex);
        if (!value.matches(regex)) {
            throw new IllegalArgumentException(value + " does not match " + regex);
        }
    }
}
