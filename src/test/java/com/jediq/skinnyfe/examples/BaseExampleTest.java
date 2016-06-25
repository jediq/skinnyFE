package com.jediq.skinnyfe.examples;

import com.jediq.skinnyfe.SkinnyFE;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import org.junit.*;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;

/**
 * json-server --port 8009 --watch db.json
 */
public abstract class BaseExampleTest {

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
        process = startupJsonServer();
        String folder = examplesLocation + getExampleNumber() + "/";

        skinnyFE = new SkinnyFE(folder + "config.json");
        skinnyFE.getConfig().setBaseLocation(folder);

        port = initialPort + getExampleNumber();
        skinnyFE.startServer(port);

        httpClient = new HttpClient();
        httpClient.start();
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

    public  Process startupJsonServer() throws Exception {
        String command = "json-server --port 8009 --watch src/main/documentation/examples/db.json";
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor(500, TimeUnit.MILLISECONDS);
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
