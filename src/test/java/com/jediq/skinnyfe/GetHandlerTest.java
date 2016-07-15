package com.jediq.skinnyfe;

import java.net.HttpCookie;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import org.junit.*;

/**
 *
 */
public class GetHandlerTest {

    private static int PORT = 8007;
    private static String BASE_URL;
    private static SkinnyFE skinnyFE;
    private static String path = "src/test/resources/basic/";

    private FixedResponseJetty vehicleEndpoint;
    private FixedResponseJetty userEndpoint;
    private HttpClient httpClient;

    @BeforeClass
    public static void setup() throws Exception {
        skinnyFE = new SkinnyFE(path + "config.json");
        skinnyFE.startServer(PORT);

        BASE_URL = "http://localhost:" + PORT + "/";
    }

    @Before
    public void before() throws Exception {

        vehicleEndpoint = new FixedResponseJetty(9019);
        vehicleEndpoint.start();
        String vehicleAsJson = new String(Files.readAllBytes(Paths.get(path, "endpoints", "vehicle.json")));
        vehicleEndpoint.addResponseString(vehicleAsJson, "text/html");

        userEndpoint = new FixedResponseJetty(9020);
        userEndpoint.start();
        String userAsJson = new String(Files.readAllBytes(Paths.get(path, "endpoints", "user.json")));
        userEndpoint.addResponseString(userAsJson, "text/html");

        httpClient = new HttpClient();
        httpClient.start();

    }

    @After
    public void after() throws Exception {
        vehicleEndpoint.close();
        userEndpoint.close();
        httpClient.stop();
    }

    @Test
    public void testEndToEnd() throws Exception {
        ContentResponse response = httpClient.GET(BASE_URL);
        assertThat(response.getStatus(), is(200));

        String content = response.getContentAsString();
        assertThat(content, startsWith("<!doctype html>"));
        assertThat(content, containsString("Car: FR123JON"));
        assertThat(content, containsString("Driver: Fred Jones"));
    }

    @Test
    public void testEndToEndFailingValidation() throws Exception {
        ContentResponse response = httpClient.GET(BASE_URL + "?regexingParam=fail");
        assertThat(response.getStatus(), is(200));

        String content = response.getContentAsString();
        assertThat(content, startsWith("<!doctype html>"));
        assertThat(content, containsString("Car: FR123JON"));
        assertThat(content, containsString("Driver:"));
    }

    @Test
    public void testEndToEndPassingValidation() throws Exception {
        ContentResponse response = httpClient.GET(BASE_URL + "?regexingParam=pass");
        assertThat(response.getStatus(), is(200));

        String content = response.getContentAsString();
        assertThat(content, startsWith("<!doctype html>"));
        assertThat(content, containsString("Car: FR123JON"));
        assertThat(content, containsString("Driver: Fred Jones"));
    }

    @Test
    public void testEndToEndFailingValidationViaCookie() throws Exception {
        ContentResponse response = httpClient.newRequest(BASE_URL).cookie(new HttpCookie("regexingParam", "fail")).send();
        assertThat(response.getStatus(), is(200));

        String content = response.getContentAsString();
        assertThat(content, startsWith("<!doctype html>"));
        assertThat(content, containsString("Car: FR123JON"));
        assertThat(content, containsString("Driver:"));
    }

    @Test
    public void testEndToEndPassingValidationViaCookie() throws Exception {
        ContentResponse response = httpClient.newRequest(BASE_URL).cookie(new HttpCookie("regexingParam", "pass")).send();

        assertThat(response.getStatus(), is(200));
        String content = response.getContentAsString();
        assertThat(content, startsWith("<!doctype html>"));
        assertThat(content, containsString("Car: FR123JON"));
        assertThat(content, containsString("Driver: Fred Jones"));
    }

    @Test
    public void testRequestAsset() throws Exception {
        ContentResponse assetResponse = httpClient.GET(BASE_URL + "assets/plain.txt");
        assertThat(assetResponse.getStatus(), is(200));
        assertThat(assetResponse.getContentAsString(), is("Plain text file\n"));
    }

    @Test
    public void testEndToEndForUnknownTemplate() throws Exception {
        ContentResponse response = httpClient.GET(BASE_URL + "/bananas");
        assertThat(response.getStatus(), is(404));
    }

    @Test
    public void testGetWithEnricher() throws Exception {
        ContentResponse response = httpClient.GET(BASE_URL + "enricherView");
        String content = response.getContentAsString();

        assertThat(content, startsWith("<!doctype html>"));
        assertThat(content, containsString("Car: FR123JON"));
        assertThat(content, containsString("Driver: Fred Jones"));
        assertThat(content, containsString("Fruit: Banana"));
    }

    @Test
    public void testGetWithEnricherChangingTemplate() throws Exception {
        ContentResponse response = httpClient.GET(BASE_URL + "enricherChangingResource");
        String content = response.getContentAsString();

        assertThat(content, startsWith("<!doctype html>"));
        assertThat(content, containsString("Car: FR123JON"));
        assertThat(content, containsString("Driver: Fred Jones"));
        assertThat(content, not(containsString("Fruit: Banana")));
    }

    @Test
    public void testGetWithEnricherChangingTemplateToAnUnknownTemplate() throws Exception {
        ContentResponse response = httpClient.GET(BASE_URL + "enricherChangingResourceBadly");
        String content = response.getContentAsString();
        assertThat(response.getStatus(), is(404));
        assertThat(content, is(""));
    }

    @Test
    public void testEndToEndPathResource() throws Exception {
        ContentResponse goodResponse = httpClient.GET(BASE_URL + "pathed/12345");
        assertThat(goodResponse.getStatus(), is(200));

        ContentResponse badResponse = httpClient.GET(BASE_URL + "pathed/23456");
        assertThat(badResponse.getStatus(), is(404));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        skinnyFE.stopServer(PORT);
    }
}