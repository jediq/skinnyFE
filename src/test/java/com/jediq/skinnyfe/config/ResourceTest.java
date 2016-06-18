package com.jediq.skinnyfe.config;

import com.github.jknack.handlebars.Handlebars;
import com.jediq.skinnyfe.Request;
import com.jediq.skinnyfe.WrappedException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import junitx.util.PrivateAccessor;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Matchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class ResourceTest {

    @Test
    public void testGetEnrichedUrl() {
        Resource resource = new Resource();
        resource.setUrl("http://example.com/{{enrich1}}/{{enrich2}}");
        String expected = "http://example.com/thing1/thing2";

        Map<String, Object> map = new HashMap<>();
        map.put("enrich1", "thing1");
        map.put("enrich2", "thing2");
        String actual = resource.getResolvedUrl(map);
        Assert.assertThat(actual, CoreMatchers.is(expected));
    }

    @Test
    public void testGetEnrichedUrl_usingHeaderFromRequest() {
        Resource resource = new Resource();
        resource.setUrl("http://example.com/{{HEADER.reference}}");
        String expected = "http://example.com/thing1";

        Request request = new Request();
        request.getHeaders().put("reference", "thing1");
        String actual = resource.getResolvedUrl("12345", request);
        Assert.assertThat(actual, CoreMatchers.is(expected));
    }

    @Test
    public void testGetEnrichedUrl_usingPathFromRequest() {
        Resource resource = new Resource();
        // Path is 1 based so element 0 is empty
        resource.setUrl("http://example.com/{{PATH.1}}");
        String expected = "http://example.com/23456";

        Request request = new Request();
        request.getPath().add("12345");
        request.getPath().add("23456");
        String actual = resource.getResolvedUrl("01234", request);
        Assert.assertThat(actual, CoreMatchers.is(expected));
    }

    @Test(expected=WrappedException.class)
    public void testGetEnrichedUrl_HandlerbarsHasAProblem() throws Exception {
        Resource resource = new Resource();
        Handlebars handlebars = Mockito.mock(Handlebars.class);
        Mockito.when(handlebars.compileInline(any(String.class))).thenThrow(new IOException());
        PrivateAccessor.setField(resource, "handlebars", handlebars);
        resource.setUrl("http://example.com/{{enrich1}}/{{enrich2}}");

        Map<String, Object> map = new HashMap<>();
        map.put("enrich1", "thing1");
        map.put("enrich2", "thing2");
        resource.getResolvedUrl(map);
    }

}