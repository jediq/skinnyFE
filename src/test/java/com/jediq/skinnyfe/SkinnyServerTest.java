package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import junitx.util.PrivateAccessor;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 */
public class SkinnyServerTest {


    @Test
    public void testErrorHandling() throws Exception {

        HttpClient httpClient = new HttpClient();
        httpClient.start();

        Config config = new Config();
        config.getErrorPages().put(404, "src/test/resources/basic/assets/404.html");
        SkinnyServer skinnyServer = new SkinnyServer(7890, config);
        skinnyServer.start();

        ContentResponse response = httpClient.GET("http://localhost:7890/enricherChangingResource");
        String content = response.getContentAsString();

        assertThat(content, is("ballas"));

        httpClient.stop();
        skinnyServer.stop();
    }

    @Test
    public void testErrorHandlingWithoutMappedPage() throws Exception {

        HttpClient httpClient = new HttpClient();
        httpClient.start();

        Config config = new Config();
        config.getErrorPages().put(408, "src/test/resources/basic/assets/404.html");
        SkinnyServer skinnyServer = new SkinnyServer(7890, config);
        skinnyServer.start();

        ContentResponse response = httpClient.GET("http://localhost:7890/enricherChangingResource");
        String content = response.getContentAsString();

        assertThat(content, is(""));

        httpClient.stop();
        skinnyServer.stop();
    }


    @Test
    public void testErrorHandling2() throws Exception {

        HttpClient httpClient = new HttpClient();
        httpClient.start();

        Server server = new Server(8908);

        HandlerCollection handlerCollection = new HandlerCollection();
        HandlerList handlerList = new HandlerList();
        handlerCollection.setHandlers(new Handler[] { handlerList });
        server.setHandler(handlerCollection);


        server.start();

        ErrorHandler errorHandler = new ErrorHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
                response.getOutputStream().print("handled");
            }
        };
        server.addBean(errorHandler);

        ContentResponse response = httpClient.GET("http://localhost:8908");

        assertThat(response.getContentAsString(), startsWith("handled"));




    }




        @Test(expected=WrappedException.class)
    public void testConstructor_serverThrowsARuntimeException() throws Exception {
        LocalSkinnyServer.localServer.set(null);
        new LocalSkinnyServer();
        fail(); // should never get here
    }

    @Test(expected=WrappedException.class)
    public void testConstructor_startThrowsARuntimeException() throws Exception {
        LocalSkinnyServer.localServer.set(new Server());
        SkinnyServer skinnyServer = new LocalSkinnyServer();
        PrivateAccessor.setField(skinnyServer, "server", null);
        skinnyServer.start();
        fail(); // should never get here
    }

    @Test(expected=WrappedException.class)
    public void testConstructor_stopThrowsARuntimeException() throws Exception {
        LocalSkinnyServer.localServer.set(new Server());
        SkinnyServer skinnyServer = new LocalSkinnyServer();
        PrivateAccessor.setField(skinnyServer, "server", null);
        skinnyServer.stop();
        fail(); // should never get here
    }

    private static class LocalSkinnyServer extends SkinnyServer {

        // Need to use a ThreadLocal here as constructServer can't be modified prior to the super
        // constructor being called.  ThreadLocal allows our tests to run in parallel with
        // deterministic results.
        static ThreadLocal <Server> localServer = new ThreadLocal<>();

        public LocalSkinnyServer() {
            super(8800, new Config());
        }

        @Override
        protected Server constructServer(int port) {
            return localServer.get();
        }
    }

}