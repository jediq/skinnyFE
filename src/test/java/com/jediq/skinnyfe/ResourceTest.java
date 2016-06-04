package com.jediq.skinnyfe;

import java.util.HashMap;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import org.junit.Test;

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
        assertThat(actual, is(expected));
    }

}