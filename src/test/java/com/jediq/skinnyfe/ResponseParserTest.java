package com.jediq.skinnyfe;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ResponseParserTest {

    ResponseParser responseParser = new ResponseParser();

    @Test
    public void testParseValidResponse() {

        ResourceResponse response = new ResourceResponse();
        response.content = "{'valid':'jsonobject'}".replaceAll("'", "\"");
        ObjectNode jsonNode = responseParser.parseJsonFromResponse(response);
        assertThat(jsonNode.isObject(), is(true));
    }

    @Test
    public void testParseInValidResponse() {

        ResourceResponse response = new ResourceResponse();
        response.content = "hulcity";
        ObjectNode jsonNode = responseParser.parseJsonFromResponse(response);
        assertThat(jsonNode.isObject(), is(true));
        assertThat(jsonNode.size(), is(0));
    }


}