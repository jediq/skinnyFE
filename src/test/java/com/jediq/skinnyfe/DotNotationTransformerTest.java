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

    @Test
    public void testToJson() {
        Map<String, String> values = new HashMap<>();
        values.put("root", "thing");
        values.put("another", "thing2");
        DotNotationTransformer dnt = new DotNotationTransformer();
        String json = dnt.toJson(values).toString();
        assertThat(json, is(swapQuote("{'root':'thing','another':'thing2'}")));
    }

    @Test
    public void testToJson_multiLevel() {
        Map<String, String> values = new HashMap<>();
        values.put("root", "thing");
        values.put("level1.level2", "thing2");
        values.put("level1.level3", "thing3");
        DotNotationTransformer dnt = new DotNotationTransformer();
        String json = dnt.toJson(values).toString();
        assertThat(json, is(swapQuote("{'level1':{'level2':'thing2','level3':'thing3'},'root':'thing'}")));
    }

    private String swapQuote(String s) {
        return s.replaceAll("'", "\\\"");
    }
}