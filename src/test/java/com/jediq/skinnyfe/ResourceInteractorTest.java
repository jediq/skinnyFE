package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.Meta;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

    @Test(expected=WrappedException.class)
    public void testLoadResource_nonExistantResource() throws Exception {
        ResourceInteractor resourceInteractor = new ResourceInteractor(new Config());
        Meta meta = new Meta();
        meta.setResource("banana");
        Request request = new Request();
        resourceInteractor.loadResources(Arrays.asList(meta), request);
    }

    @Test(expected=WrappedException.class)
    public void testSaveResource_nonExistantResource() throws Exception {
        ResourceInteractor resourceInteractor = new ResourceInteractor(new Config());
        Meta meta = new Meta();
        meta.setResource("banana");
        Request request = new Request();
        Map<Meta, String> metaMap = new HashMap<>();
        metaMap.put(meta, "split");
        resourceInteractor.saveResources(metaMap, request);
    }



}