package com.jediq.skinnyfe.config;

import com.github.jknack.handlebars.Handlebars;
import com.jediq.skinnyfe.WrappedException;
import com.jediq.skinnyfe.config.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import junitx.util.PrivateAccessor;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.is;
import org.hamcrest.Matchers;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Matchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class ResourceTest {

    @Test
    public void testGetEnrichedUrl() {
        Resource resource = new Resource();
        resource.setUrl("http://example.com/{{enrich1}}/{{enrich2}}");
        String expected = "http://example.com/thing1/thing2";

        Map<String, String> map = new HashMap<>();
        map.put("enrich1", "thing1");
        map.put("enrich2", "thing2");
        String actual = resource.getEnrichedUrl(map);
        Assert.assertThat(actual, CoreMatchers.is(expected));
    }

    @Test(expected=WrappedException.class)
    public void testGetEnrichedUrl_HandlerbarsHasAProblem() throws Exception {
        Resource resource = new Resource();
        Handlebars handlebars = Mockito.mock(Handlebars.class);
        Mockito.when(handlebars.compileInline(any(String.class))).thenThrow(new IOException());
        PrivateAccessor.setField(resource, "handlebars", handlebars);
        resource.setUrl("http://example.com/{{enrich1}}/{{enrich2}}");

        Map<String, String> map = new HashMap<>();
        map.put("enrich1", "thing1");
        map.put("enrich2", "thing2");
        resource.getEnrichedUrl(map);
    }

}