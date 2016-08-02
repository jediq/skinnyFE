package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import org.eclipse.jetty.client.HttpClient;
import static org.hamcrest.Matchers.containsString;
import org.junit.After;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 */
public class SkinnyAdminServerTest {

    private static final int PORT = 8054;
    public static final String URI = "http://localhost:" + PORT;

    private HttpClient httpClient;

    @Before
    public void before() throws Exception{
        httpClient = new HttpClient();
        httpClient.start();
    }

    @After
    public void after() throws Exception {
        httpClient.stop();
    }

    @Test
    public void testHealthCheck() throws Exception {

        SkinnyAdminServer skinnyAdminServer = new SkinnyAdminServer(PORT, new MetricRegistry());
        skinnyAdminServer.start();

        try {

            String contentAsString = httpClient.GET(URI + "/healthcheck").getContentAsString();
            assertThat(contentAsString, containsString("\"admin\":{\"healthy\":true}"));

        } finally {
            skinnyAdminServer.stop();
        }
    }

    @Test
    public void testMetrics() throws Exception {
        SkinnyAdminServer skinnyAdminServer = new SkinnyAdminServer(PORT,  new MetricRegistry());
        skinnyAdminServer.start();

        try {
            String contentAsString = httpClient.GET(URI + "/metrics").getContentAsString();
            assertThat(contentAsString, containsString("{\"version\":\"3.0.0\",\"gauges\":"));
        } finally {
            skinnyAdminServer.stop();
        }
    }

}