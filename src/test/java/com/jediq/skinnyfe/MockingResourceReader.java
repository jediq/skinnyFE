package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.resource.ResourceReader;
import org.eclipse.jetty.client.HttpClient;

/**
 *
 */
public class MockingResourceReader extends ResourceReader {

    private static ThreadLocal<HttpClient> passedInHttpClient = new ThreadLocal<>();

    public MockingResourceReader(HttpClient httpClient) {
        super(new Config(), new MetricRegistry());
        this.passedInHttpClient.set(httpClient);
    }

    @Override
    protected HttpClient createHttpClient() {
        return passedInHttpClient.get();
    }
}
