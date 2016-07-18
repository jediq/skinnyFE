package com.jediq.skinnyfe.examples;

import com.jediq.skinnyfe.SkinnyFE;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import static org.junit.Assume.assumeTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The example tests require json-server to be installed on the executing machine.
 *
 * If json-server is not installed, the tests will not fail but will also
 * not carry on as it uses an assume().
 *
 * This can be installed with :
 *
 *      npm install -g json-server
 *
 *
 */
public abstract class BaseExampleTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String examplesLocation = "src/main/documentation/examples/example";
    private String baseUrl = "http://localhost:";
    private int initialPort = 8550;

    protected Process process;
    protected SkinnyFE skinnyFE;
    protected HttpClient httpClient;
    protected int port;

    public abstract int getExampleNumber();

    @Before
    public void start() throws Exception {
        httpClient = new HttpClient();
        httpClient.start();

        process = startupJsonServer();
        String folder = examplesLocation + getExampleNumber() + "/";

        skinnyFE = new SkinnyFE(folder + "config.json");
        skinnyFE.getConfig().setBaseLocation(folder);

        port = initialPort + getExampleNumber();
        skinnyFE.startServer(port);
    }

    @BeforeClass
    public static void jsonServerInstalled() throws Exception {
        try {
            Runtime.getRuntime().exec("json-server");
        } catch (IOException e) {
            assumeTrue(false);
        }
    }

    protected String load(String path) throws Exception {

        ContentResponse response = httpClient.GET(baseUrl + port + "/" + path);
        assertThat(response.getStatus(), is(200));

        String content = response.getContentAsString();
        assertThat(content, startsWith("<!doctype html>"));

        return content;
    }

    protected String post(String path, Map<String, String> formFields) throws Exception {
        Request post = httpClient.POST(baseUrl + port + "/" + path);
        formFields.forEach(post::param);
        ContentResponse response = post.send();
        return response.getContentAsString();
    }

    public  Process startupJsonServer() throws Exception {
        String command = "json-server --port 8009 --watch src/main/documentation/examples/db.json";
        logger.info("Starting Json Server with : {}", command);
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor(500, TimeUnit.MILLISECONDS);
        ContentResponse checkResponse;
        do {
            Thread.sleep(500);
            String uri = "http://localhost:8009/posts";
            checkResponse = httpClient.GET(uri);
            logger.info("Json Server is alive at {}? : {}", uri, checkResponse.getStatus());
        } while (checkResponse.getStatus() != 200);

        return process;

    }

    @After
    public void after() throws Exception {
        httpClient.stop();
        skinnyFE.stopServer(port);
        process.destroy();
        process.waitFor();
    }

}
