package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import org.eclipse.jetty.client.HttpClient;

/**
 *
 */
public class MockingResourceInteractor extends ResourceInteractor {

    private static ThreadLocal<HttpClient> passedInHttpClient = new ThreadLocal<>();

    public MockingResourceInteractor(HttpClient httpClient) {
        super(new Config());
        this.passedInHttpClient.set(httpClient);
    }

    @Override
    protected HttpClient createHttpClient() {
        return passedInHttpClient.get();
    }
}
