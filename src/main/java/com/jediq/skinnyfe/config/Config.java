package com.jediq.skinnyfe.config;

import com.jediq.skinnyfe.WrappedException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 */
public class Config {

    private int port;
    private int adminPort;
    private String defaultTemplates = ".";
    private String defaultFragments = ".";
    private List <Resource> resources = new ArrayList<>();
    private List <SkinnyTemplate> templates = new ArrayList<>();
    private long millisToCacheTemplates = 0;
    private String assetsPath;
    private String assetsFolder;
    private String baseLocation = "";
    private String name = "NO-NAME";
    private Map<Integer, String> errorPages = new HashMap<>();

    public static Config load(String configLocation) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Config config = objectMapper.readValue(new File(configLocation), Config.class);
            if (config.getPort() == 0) {
                throw new IllegalStateException("At the very least the port needs to be set, see the documentaiton.");
            }
            return config;
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
        return getBaseLocation() + defaultTemplates;
    }

    public void setDefaultTemplates(String defaultTemplates) {
        this.defaultTemplates = defaultTemplates;
    }

    public String getDefaultFragments() {
        return defaultFragments;
    }

    public void setDefaultFragments(String defaultFragments) {
        this.defaultFragments = defaultFragments;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getAdminPort() {
        return adminPort;
    }

    public void setAdminPort(int adminPort) {
        this.adminPort = adminPort;
    }

    public String getAssetsPath() {
        return assetsPath;
    }

    public void setAssetsPath(String assetsPath) {
        this.assetsPath = assetsPath;
    }

    public String getAssetsFolder() {
        if (assetsFolder != null) {
            return getBaseLocation() + assetsFolder;
        } else {
            return null;
        }
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

    public void setBaseLocation(String baseLocation) {
        this.baseLocation = baseLocation;
    }

    public String getBaseLocation() {
        return baseLocation;
    }

    public Map<Integer, String> getErrorPages() {
        return errorPages;
    }

    public void setErrorPages(Map<Integer, String> errorPages) {
        this.errorPages = errorPages;
    }

    public long getMillisToCacheTemplates() {
        return millisToCacheTemplates;
    }

    public void setMillisToCacheTemplates(long millisToCacheTemplates) {
        this.millisToCacheTemplates = millisToCacheTemplates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
