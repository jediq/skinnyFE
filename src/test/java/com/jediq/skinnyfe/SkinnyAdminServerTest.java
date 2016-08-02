package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.jediq.skinnyfe.config.Config;
import org.eclipse.jetty.client.HttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

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