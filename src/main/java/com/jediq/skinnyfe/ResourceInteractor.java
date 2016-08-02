package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import static com.codahale.metrics.MetricRegistry.name;
import com.codahale.metrics.Timer;
import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.Meta;
import com.jediq.skinnyfe.config.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ResourceInteractor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HttpClient httpClient;
    private final Config config;
    private final MetricRegistry metrics;

    public ResourceInteractor(Config config, MetricRegistry metrics) {
        this.config = config;
        this.metrics = metrics;
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

    private ResourceResponse loadResource(Meta meta, Request request) {
        try {
            logger.info("Loading resource from : " + meta.getResource());
            Resource resource = findResource(meta.getResource());

            boolean valid = true;
            for (String key : request.getParams().keySet()) {
                valid &= resource.validateInput(key, request.getParams().get(key));
            }
            for (String key : request.getHeaders().keySet()) {
                valid &= resource.validateInput(key, request.getHeaders().get(key));
            }

            if (!valid) {
                logger.debug("Input data did not validate");
                ResourceResponse resourceResponse = new ResourceResponse();
                resourceResponse.code = 400;
                resourceResponse.reason = "{ \"error\":\"Input data did not validate\" }";
                resourceResponse.content = "{}";
                resourceResponse.url = resource.getUrl();
                return resourceResponse;
            }

            String enrichedUrl = resource.getResolvedUrl(meta.getIdentifier(), request);
            logger.info("Requesting resource from : " + enrichedUrl);

            org.eclipse.jetty.client.api.Request httpRequest = httpClient.newRequest(enrichedUrl);
            httpRequest.method(HttpMethod.GET);

            resource.getHeaders().forEach(httpRequest::header);
            logger.debug("Sending {} headers with the request", httpRequest.getHeaders().size());

            final Timer timer = metrics.timer(name(ResourceInteractor.class, meta.getResource(), "get-requests"));

            final Timer.Context context = timer.time();
            ContentResponse contentResponse;
            try {
                contentResponse = httpRequest.send();
            } finally {
                context.stop();
            }

            ResourceResponse resourceResponse = new ResourceResponse();
            resourceResponse.code = contentResponse.getStatus();
            resourceResponse.content = contentResponse.getContentAsString();
            resourceResponse.reason = contentResponse.getReason();
            resourceResponse.url = enrichedUrl;
            contentResponse.getHeaders()
                    .forEach(field -> resourceResponse.headers.put(field.getName(), field.getValue()));

            return resourceResponse;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new WrappedException(e);
        }
    }

    private int saveResource(Meta meta, String string, Request request) {
        try {

            logger.info("Loading resource from : " + meta.getResource());
            Resource resource = findResource(meta.getResource());

            String enrichedUrl = resource.getResolvedUrl(meta.getIdentifier(), request);

            final Timer timer = metrics.timer(name(ResourceInteractor.class, meta.getResource(), "post-requests"));

            final Timer.Context context = timer.time();
            ContentResponse response;
            try {
                response = httpClient.POST(enrichedUrl)
                        .content(new StringContentProvider(string), "application/json")
                        .send();
            } finally {
                context.stop();
            }

            return response.getStatus();

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
