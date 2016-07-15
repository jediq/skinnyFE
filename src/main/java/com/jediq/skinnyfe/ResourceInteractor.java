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
        httpClient = createHttpClient();

        try {
            httpClient.start();
        } catch (Exception e) {
            throw new WrappedException(e);
        }
    }

    protected HttpClient createHttpClient() {
        return new HttpClient(new SslContextFactory());
    }

    public Map<Meta, ResourceResponse> loadResources(List<Meta> metaList, Request request) {
        Map <Meta, ResourceResponse> metaMap = new HashMap<>();
        for (Meta meta : metaList) {
            metaMap.put(meta, loadResource(meta, request));
        }
        return metaMap;
    }

    public void saveResources(Map<Meta, String> metaMap, Request request) {
        for (Meta meta : metaMap.keySet()) {
            saveResource(meta, metaMap.get(meta), request);
        }
    }

    private int saveResource(Meta meta, String string, Request request) {
        try {

            logger.info("Loading resource from : " + meta.getResource());
            Resource resource = findResource(meta.getResource());

            String enrichedUrl = resource.getResolvedUrl(meta.getIdentifier(), request);
            ContentResponse response = httpClient.POST(enrichedUrl)
                    .content(new StringContentProvider(string), "application/json")
                    .send();
            return response.getStatus();

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new WrappedException(e);
        }
    }

    private ResourceResponse loadResource(Meta meta, Request request) {
        try {
            logger.info("Loading resource from : " + meta.getResource());
            Resource resource = findResource(meta.getResource());

            try {
                request.getParams().forEach(resource::validateInput);
                resource.getHeaders().forEach(resource::validateInput);
            } catch (IllegalArgumentException e) {
                logger.debug("Input data did not validate", e);
                ResourceResponse resourceResponse = new ResourceResponse();
                resourceResponse.code = 400;
                resourceResponse.reason = "{ \"error\":\"Input data did not validate\" }";
                resourceResponse.content = "{}";
                return resourceResponse;
            }

            String enrichedUrl = resource.getResolvedUrl(meta.getIdentifier(), request);
            logger.info("Requesting resource from : " + enrichedUrl);

            org.eclipse.jetty.client.api.Request httpRequest = httpClient.newRequest(enrichedUrl);
            httpRequest.method(HttpMethod.GET);

            resource.getHeaders().forEach(httpRequest::header);
            logger.debug("Sending {} headers with the request", httpRequest.getHeaders().size());
            ContentResponse contentResponse = httpRequest.send();

            ResourceResponse resourceResponse = new ResourceResponse();
            resourceResponse.code = contentResponse.getStatus();
            resourceResponse.content = contentResponse.getContentAsString();
            resourceResponse.reason = contentResponse.getReason();
            contentResponse.getHeaders()
                    .forEach(field -> resourceResponse.headers.put(field.getName(), field.getValue()));

            return resourceResponse;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new WrappedException(e);
        }
    }

    private Resource findResource(String resourceName) throws ExecutionException {
        for (Resource resource : config.getResources()) {
            if (resourceName.equals(resource.getName())) {
                return resource;
            }
        }
        throw new ExecutionException(new IllegalArgumentException("Could not find resource for : " + resourceName));
    }
}
