package com.jediq.skinnyfe.resource;

import com.codahale.metrics.MetricRegistry;
import com.jediq.skinnyfe.WrappedException;
import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.Resource;
import java.util.concurrent.ExecutionException;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ResourceInteractor {


    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final HttpClient httpClient;
    protected final Config config;
    protected final MetricRegistry metrics;

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

    protected Resource findResource(String resourceName) throws ExecutionException {
        for (Resource resource : config.getResources()) {
            if (resourceName.equals(resource.getName())) {
                return resource;
            }
        }
        throw new ExecutionException(new IllegalArgumentException("Could not find resource for : " + resourceName));
    }

    protected HttpClient createHttpClient() {
        return new HttpClient(new SslContextFactory());
    }

}
