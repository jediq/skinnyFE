package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.Meta;
import com.jediq.skinnyfe.config.Resource;
import com.jediq.skinnyfe.resource.ResourceReader;
import com.jediq.skinnyfe.resource.ResourceResponse;
import java.util.Arrays;
import java.util.Map;
import org.eclipse.jetty.client.HttpClient;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import org.junit.Test;

/**
 *
 */
public class ResourceReaderTest {

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
    public void testLoadResource_nonExistantResource() throws Exception {
        ResourceReader resourceReader = new ResourceReader(new Config(), new MetricRegistry());
        Meta meta = new Meta();
        meta.setResource(RESOURCE_NAME);
        Request request = new Request();
        resourceReader.loadResources(Arrays.asList(meta), request);
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

        ResourceReader resourceReader = new ResourceReader(config, new MetricRegistry());

        Meta meta = new Meta();
        meta.setResource(RESOURCE_NAME);

        Map<Meta, ResourceResponse> resourceResponseMap = resourceReader.loadResources(Arrays.asList(meta), request);
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

        ResourceReader resourceReader = new ResourceReader(config, new MetricRegistry());

        Meta meta = new Meta();
        meta.setResource(RESOURCE_NAME);

        Map<Meta, ResourceResponse> resourceResponseMap = resourceReader.loadResources(Arrays.asList(meta), request);
        assertThat(resourceResponseMap.size(), is(1));
        ResourceResponse response = resourceResponseMap.values().iterator().next();

        assertThat(response.code, is(400));
    }

}