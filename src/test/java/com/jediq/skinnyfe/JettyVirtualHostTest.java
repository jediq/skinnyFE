package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.jediq.skinnyfe.config.Config;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Ignore;
import org.junit.Test;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * NOTE : This class requires, jeqiq1.local and jediq2.local to be in the host file
 * pointing to 127.0.0.1
 */
@Ignore
public class JettyVirtualHostTest {

    public static final String RESOURCES = "src/test/resources/virtual_hosts/";
    private String configLocation = "src/test/resources/basic/config.json";

    @Test
    public void test() throws Exception {


        HttpClient httpClient = new HttpClient();

        Server server = new Server(6786);
        try {

            ContextHandler jediq1 = createVirtualHandler("jediq1");
            ContextHandler jediq2 = createVirtualHandler("jediq2");

            HandlerCollection handlerCollection = new HandlerCollection();
            handlerCollection.addHandler(jediq1);
            handlerCollection.addHandler(jediq2);

            server.setHandler(handlerCollection);

            httpClient.start();
            server.start();

            ContentResponse response1 = httpClient.GET("http://jediq1.local:6786/resource");
            assertThat(response1.getStatus(), is(200));
            assertThat(new String(response1.getContent()), is("resource-jediq1"));

            ContentResponse response2 = httpClient.GET("http://jediq1.local:6786");
            assertThat(response2.getStatus(), is(200));
            assertThat(new String(response2.getContent()), is("skinny-jediq1"));

            ContentResponse response3 = httpClient.GET("http://jediq2.local:6786/resource");
            assertThat(response3.getStatus(), is(200));
            assertThat(new String(response3.getContent()), is("resource-jediq2"));

            ContentResponse response4 = httpClient.GET("http://jediq2.local:6786");
            assertThat(response4.getStatus(), is(200));
            assertThat(new String(response4.getContent()), is("skinny-jediq2"));


        } finally {
            server.stop();
            httpClient.stop();
        }
    }

    private ContextHandler createVirtualHandler(String host) {
        HandlerCollection handlerCollection = new HandlerCollection();

 //       handlerCollection.addHandler(makeSkinnyHandler("/*", handlerCollection ));
        handlerCollection.addHandler(makeResourceHandler("/resource", RESOURCES + host, handlerCollection ));

        ContextHandler contextHandler = new ContextHandler();
        contextHandler.setVirtualHosts(new String[] { host + ".local" });
        contextHandler.setHandler(handlerCollection);
        return contextHandler;
    }

    private Handler makeSkinnyHandler(String path, HandlerCollection parent) {
        Handler handler = new SkinnyHandler(Config.load(configLocation), new MetricRegistry());

//        ContextHandler contextHandler = new ServletContextHandler(parent, path);
//        contextHandler.setHandler(handler);

        return handler;
    }


    private Handler makeResourceHandler(String path, String folder, HandlerContainer parent) {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(folder);
//
//        ContextHandler contextHandler = new ServletContextHandler(parent, path);
//        contextHandler.setHandler(resourceHandler);

        return resourceHandler;
    }

}
