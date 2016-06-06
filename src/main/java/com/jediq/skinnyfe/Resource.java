package com.jediq.skinnyfe;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Resource {

    private String name;
    private String url;
    private String methods = "GET";

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

    public String getEnrichedUrl(String identifier) {
        Map <String, String> map = new HashMap<>();
        map.put("identifier", identifier);
        return getEnrichedUrl(map);
    }

        public String getEnrichedUrl(Map<String, String> enrichmentValues) {
        try {
            Template template = handlebars.compileInline(getUrl());
            return template.apply(enrichmentValues);
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }
}
