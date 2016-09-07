package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.jediq.skinnyfe.config.Config;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.Ignore;
import org.junit.Test;

import static com.jediq.skinnyfe.TestUtil.assumePing;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * NOTE : This class requires, skinny1.local and skinny2.local to be resolvable to localhost
 *

 sudo vi /etc/hosts

 skinny1.local   localhost
 skinny2.local   localhost

 *
 */
public class VirtualHostTest {

    public static final String RESOURCES = "src/test/resources/virtual_hosts/";

    HttpClient httpClient = new HttpClient();

    @Test
    public void test() throws Exception {

        assumePing("skinny1.local");
        assumePing("skinny2.local");

        SkinnyMainServer server = new SkinnyMainServer(6786, new MetricRegistry());
        try {

            httpClient.start();

            server.start();

            server.addConfiguration(createConfig("skinny1"));
            server.addConfiguration(createConfig("skinny2"));

            validateResponse("http://skinny1.local:6786", "skinny1\n");
            validateResponse("http://skinny2.local:6786", "skinny2\n");
        } finally {
            server.stop();
            httpClient.stop();
        }
    }

    private void validateResponse(String uri, String value) throws Exception {
        ContentResponse response1 = httpClient.GET(uri);
        assertThat(response1.getStatus(), is(200));
        assertThat(new String(response1.getContent()), is(value));
    }

    private Config createConfig(String host) {
        Config config = new Config();
        config.setName(host);
        config.setVirtualHost(host + ".local");
        config.setDefaultTemplates(RESOURCES + host);
        return config;
    }


}
