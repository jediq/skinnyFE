package com.jediq.skinnyfe;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import static org.hamcrest.CoreMatchers.containsString;
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
    private static String path = "src/test/resources/basic/";

    @BeforeClass
    public static void setup() throws Exception {


        skinnyFE = new SkinnyFE(path + "config.json", path + "templates");
        skinnyFE.startServer(PORT);

        BASE_URL = "http://localhost:" + PORT + "/";
    }

    @Test
    public void testEndToEnd() throws Exception {

        FixedResponseJetty vehicleEndpoint = new FixedResponseJetty(9019);
        vehicleEndpoint.start();
        String vehicleAsJson = new String(Files.readAllBytes(Paths.get(path, "endpoints", "vehicle.json")));
        vehicleEndpoint.addResponseString(vehicleAsJson, "text/html");

        FixedResponseJetty userEndpoint = new FixedResponseJetty(9020);
        userEndpoint.start();
        String userAsJson = new String(Files.readAllBytes(Paths.get(path, "endpoints", "user.json")));
        userEndpoint.addResponseString(userAsJson, "text/html");

        HttpClient httpClient = new HttpClient();
        httpClient.start();
        ContentResponse response = httpClient.GET(BASE_URL);
        assertThat(response.getStatus(), is(200));

        String content = response.getContentAsString();
        assertThat(content, startsWith("<!doctype html>"));
        assertThat(content, containsString("Car: FR123JON"));
        assertThat(content, containsString("Driver: Fred Jones"));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        skinnyFE.stopServer(PORT);
    }
}