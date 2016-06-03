package com.jediq.skinnyfe;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class SkinnyFETest {

    private static int PORT = 8007;
    private static String BASE_URL;
    private static SkinnyFE skinnyFE;

    @BeforeClass
    public static void setup() throws Exception {

        FixedResponseJetty fixedResponseJetty = new FixedResponseJetty();

        String path = "src/test/resources/basic/";
        skinnyFE = new SkinnyFE(path + "config.json", path + "templates");
        skinnyFE.startServer(PORT);

        BASE_URL = "http://localhost:" + PORT + "/";
    }

    @Test
    public void testEndToEnd() throws Exception {

        HttpClient httpClient = new HttpClient();
        httpClient.start();
        ContentResponse response = httpClient.GET(BASE_URL);
        assertThat(response.getStatus(), is(200));

        assertThat(response.getContentAsString(), startsWith("<!doctype html>"));


    }

    @AfterClass
    public static void tearDown() throws Exception {
        skinnyFE.stopServer(PORT);
    }
}