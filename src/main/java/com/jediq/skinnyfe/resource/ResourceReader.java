package com.jediq.skinnyfe.resource;

import com.codahale.metrics.MetricRegistry;
import static com.codahale.metrics.MetricRegistry.name;
import com.codahale.metrics.Timer;
import com.jediq.skinnyfe.Request;
import com.jediq.skinnyfe.WrappedException;
import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.Meta;
import com.jediq.skinnyfe.config.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.client.api.ContentResponse;

import org.eclipse.jetty.http.HttpMethod;

/**
 *
 */
public class ResourceReader extends ResourceInteractor {

    public ResourceReader(Config config, MetricRegistry metrics) {
        super(config, metrics);
    }

    public Map<Meta, ResourceResponse> loadResources(List<Meta> metaList, Request request) {
        Map <Meta, ResourceResponse> metaMap = new HashMap<>();
        for (Meta meta : metaList) {
            metaMap.put(meta, loadResource(meta, request));
        }
        return metaMap;
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

            final Timer timer = metrics.timer(name(ResourceReader.class, meta.getResource(), "get-requests"));

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
}
