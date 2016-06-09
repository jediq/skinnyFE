package com.jediq.skinnyfe;

import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 */
public class DotNotationTransformerTest {


    @Ignore
    @Test
    public void testToJson() {
        Map<String, String> values = new HashMap<>();
        values.put("root", "thing");
        DotNotationTransformer dnt = new DotNotationTransformer();
        String json = dnt.toJson(values);
        assertThat(json, is(swapQuote("{'root':'thing'}")));
    }

    private String swapQuote(String s) {
        return s.replaceAll("'", "\\\"");
    }
}