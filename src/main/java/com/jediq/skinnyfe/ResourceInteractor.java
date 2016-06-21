package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.Meta;
import com.jediq.skinnyfe.config.Resource;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 *
 */
public class ResourceInteractor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HttpClient httpClient;
    private final Config config;

    public ResourceInteractor(Config config) {
        this.config = config;
        httpClient = new HttpClient(new SslContextFactory());

        try {
            httpClient.start();
        } catch (Exception e) {
            throw new WrappedException(e);
        }
    }

    public Map<Meta, String> loadResources(List<Meta> metaList, Request request) {
        Map <Meta, String> metaMap = new HashMap<>();
        for (Meta meta : metaList) {
            String resourceData = loadResource(meta, request);
            if (!resourceData.isEmpty()) {
                metaMap.put(meta, resourceData);
            }
        }
        return metaMap;
    }

    public void saveResources(Map<Meta, String> metaMap, Request request) {
        for (Meta meta : metaMap.keySet()) {
            saveResource(meta, metaMap.get(meta), request);
        }
    }

    private boolean saveResource(Meta meta, String string, Request request) {
        try {

            logger.info("Loading resource from : " + meta.getResource());
            Resource resource = findResource(meta.getResource());
            String enrichedUrl = resource.getResolvedUrl(meta.getIdentifier(), request);
            ContentResponse response = httpClient.POST(enrichedUrl)
                    .content(new StringContentProvider(string), "application/json")
                    .send();
            return response.getStatus() == 200;

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new WrappedException(e);
        }
    }

    private String loadResource(Meta meta, Request request) {
        try {
            logger.info("Loading resource from : " + meta.getResource());
            Resource resource = findResource(meta.getResource());
            String enrichedUrl = resource.getResolvedUrl(meta.getIdentifier(), request);

            logger.info("Requesting resource from : " + enrichedUrl);

            org.eclipse.jetty.client.api.Request httpRequest = httpClient.newRequest(enrichedUrl);
            httpRequest.method(HttpMethod.GET);

            resource.getHeaders().forEach(httpRequest::header);
            logger.debug("Sending {} headers with the request", httpRequest.getHeaders().size());
            ContentResponse response = httpRequest.send();

            if (response.getStatus() != 200) {
                logger.info("Resource at '{}' responded with status : {}", enrichedUrl, response.getStatus());
                throw new BadResponseException(response.getStatus());
            }
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
        throw new IllegalArgumentException("Could not find resource for : " + resourceName);
    }
}
