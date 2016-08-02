package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.Meta;
import com.jediq.skinnyfe.resource.ResourceWriter;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Test;

/**
 *
 */
public class ResourceWriterTest {

    public static final String RESOURCE_NAME = "banana";

    @Test(expected=WrappedException.class)
    public void testConstructorFailsToStartClient() throws Exception {
        HttpClient httpClient = new HttpClient() {
            @Override
            protected void doStart() throws Exception {
                throw new IllegalArgumentException();
            }
        };
        new MockingResourceReader(httpClient);
    }

    @Test(expected=WrappedException.class)
    public void testSaveResource_nonExistantResource() throws Exception {
        ResourceWriter resourceWriter = new ResourceWriter(new Config(), new MetricRegistry());
        Meta meta = new Meta();
        meta.setResource(RESOURCE_NAME);
        Request request = new Request();
        Map<Meta, String> metaMap = new HashMap<>();
        metaMap.put(meta, "split");
        resourceWriter.saveResources(metaMap, request);
    }
}