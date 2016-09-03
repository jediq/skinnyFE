package com.jediq.skinnyfe.resource;

import com.codahale.metrics.MetricRegistry;
import static com.codahale.metrics.MetricRegistry.name;
import com.codahale.metrics.Timer;
import com.jediq.skinnyfe.Request;
import com.jediq.skinnyfe.WrappedException;
import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.Meta;
import com.jediq.skinnyfe.config.Resource;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ResourceWriter extends ResourceInteractor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ResourceWriter(Config config, MetricRegistry metrics) {
        super(config, metrics);
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

            if (internalHostProtection.isProtected(enrichedUrl)) {
                return 403;
            }

            final Timer timer = metrics.timer(name(ResourceWriter.class, meta.getResource(), "post-requests"));

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
}
