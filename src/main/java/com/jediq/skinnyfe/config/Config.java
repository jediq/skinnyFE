package com.jediq.skinnyfe.config;

import com.jediq.skinnyfe.WrappedException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 */
public class Config {

    private int port;
    private String defaultTemplates;
    private List <Resource> resources = new ArrayList<>();
    private List <SkinnyTemplate> templates = new ArrayList<>();
    private String assetsPath;
    private String assetsFolder;

    public static Config load(String configLocation) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new File(configLocation), Config.class);
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

    public List <Resource> getResources() {
        return resources;
    }

    public void setResources(List <Resource> resources) {
        this.resources = resources;
    }

    public String getDefaultTemplates() {
        return defaultTemplates;
    }

    public void setDefaultTemplates(String defaultTemplates) {
        this.defaultTemplates = defaultTemplates;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAssetsPath() {
        return assetsPath;
    }

    public void setAssetsPath(String assetsPath) {
        this.assetsPath = assetsPath;
    }

    public String getAssetsFolder() {
        return assetsFolder;
    }

    public void setAssetsFolder(String assetsFolder) {
        this.assetsFolder = assetsFolder;
    }

    public List<SkinnyTemplate> getTemplates() {
        return templates;
    }

    public void setTemplates(List<SkinnyTemplate> templates) {
        this.templates = templates;
    }
}
