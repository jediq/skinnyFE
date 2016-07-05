package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import java.io.IOException;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Test;

/**
 *
 */
public class ResourceInteractorTest {

    @Test(expected=WrappedException.class)
    public void testConstructorFailsToStartClient() throws Exception {
        HttpClient httpClient = new HttpClient() {
            @Override
            protected void doStart() throws Exception {
                throw new IllegalArgumentException();
            }
        };
        new MockingResourceInteractor(httpClient);
    }

}