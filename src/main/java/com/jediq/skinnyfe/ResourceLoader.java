package com.jediq.skinnyfe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ResourceLoader {

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HttpClient httpClient;
    private final Config config;

    public ResourceLoader(Config config) {
        this.config = config;
        httpClient = new HttpClient();

        try {
            httpClient.start();
        } catch (Exception e) {
            throw new WrappedException(e);
        }
    }

    public Map<Meta, String> loadResources(List<Meta> metaList) {
        Map <Meta, String> metaMap = new HashMap<>();
        for (Meta meta : metaList) {
            metaMap.put(meta, loadResource(meta));
        }
        return metaMap;
    }

    private String loadResource(Meta meta) {
        try {
            logger.info("Loading resource from : " + meta.getResource());
            Resource resource = findResource(meta.getResource());
            String enrichedUrl = resource.getEnrichedUrl(meta.getIdentifier());
            ContentResponse response = httpClient.GET(enrichedUrl);
            logger.info("Resource responded with status : " + response.getStatus());

            System.out.println("resource response.getContent() = " + new String(response.getContent()));
            return response.getContentAsString();

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new WrappedException(e);
        }
    }

    private Resource findResource(String resourceName) {
        for (Resource resource : config.getResources()) {
            if (resourceName.equals(resource.getName())) {
                return resource;
            }
        }
        return null;
    }
}
