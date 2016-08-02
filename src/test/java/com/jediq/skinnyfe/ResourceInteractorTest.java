package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.Meta;
import com.jediq.skinnyfe.config.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.client.HttpClient;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import org.junit.Test;

/**
 *
 */
public class ResourceInteractorTest {

    public static final String RESOURCE_NAME = "banana";

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
        ResourceInteractor resourceInteractor = new ResourceInteractor(new Config(), new MetricRegistry());
        Meta meta = new Meta();
        meta.setResource(RESOURCE_NAME);
        Request request = new Request();
        resourceInteractor.loadResources(Arrays.asList(meta), request);
    }

    @Test(expected=WrappedException.class)
    public void testSaveResource_nonExistantResource() throws Exception {
        ResourceInteractor resourceInteractor = new ResourceInteractor(new Config(), new MetricRegistry());
        Meta meta = new Meta();
        meta.setResource(RESOURCE_NAME);
        Request request = new Request();
        Map<Meta, String> metaMap = new HashMap<>();
        metaMap.put(meta, "split");
        resourceInteractor.saveResources(metaMap, request);
    }

    @Test
    public void testLoadResource_failingParamValidation() {
        Config config = new Config();
        Resource resource = new Resource();
        resource.setName(RESOURCE_NAME);
        resource.getInputValidators().put("fish", "cod");
        config.getResources().add(resource);

        Request request = new Request();
        request.setUrl(RESOURCE_NAME);
        request.getParams().put("fish", "haddock");

        ResourceInteractor resourceInteractor = new ResourceInteractor(config, new MetricRegistry());

        Meta meta = new Meta();
        meta.setResource(RESOURCE_NAME);

        Map<Meta, ResourceResponse> resourceResponseMap = resourceInteractor.loadResources(Arrays.asList(meta), request);
        assertThat(resourceResponseMap.size(), is(1));
        ResourceResponse response = resourceResponseMap.values().iterator().next();

        assertThat(response.code, is(400));
    }

    @Test
    public void testLoadResource_failingHeaderValidation() {
        Config config = new Config();
        Resource resource = new Resource();
        resource.setName(RESOURCE_NAME);
        resource.getInputValidators().put("fish", "cod");
        config.getResources().add(resource);

        Request request = new Request();
        request.setUrl(RESOURCE_NAME);
        request.getHeaders().put("fish", "haddock");

        ResourceInteractor resourceInteractor = new ResourceInteractor(config, new MetricRegistry());

        Meta meta = new Meta();
        meta.setResource(RESOURCE_NAME);

        Map<Meta, ResourceResponse> resourceResponseMap = resourceInteractor.loadResources(Arrays.asList(meta), request);
        assertThat(resourceResponseMap.size(), is(1));
        ResourceResponse response = resourceResponseMap.values().iterator().next();

        assertThat(response.code, is(400));
    }




}