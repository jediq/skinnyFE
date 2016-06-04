package com.jediq.skinnyfe;

import java.util.List;

/**
 *
 */
public class Config {

    private String templates;

    private List <Resource> resources;

    public List <Resource> getResources() {
        return resources;
    }

    public void setResources(List <Resource> resources) {
        this.resources = resources;
    }

    public String getTemplates() {
        return templates;
    }

    public void setTemplates(String templates) {
        this.templates = templates;
    }
}
