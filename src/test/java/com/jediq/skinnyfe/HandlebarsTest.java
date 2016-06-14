package com.jediq.skinnyfe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 */
public class HandlebarsTest {

    @Test
    public void testJackson() throws Exception {

        Handlebars handlebars = new Handlebars();
        handlebars.registerHelper("json", Jackson2Helper.INSTANCE);

        String json = "{\"key\":\"value\"}";
        JsonNode jsonNode = new ObjectMapper().readValue(json, JsonNode.class);

        Context context = Context.newBuilder(jsonNode)
                        .resolver(JsonNodeValueResolver.INSTANCE).build();

        Template template = handlebars.compileInline(":{{key}}:");
        String rendered = template.apply(context);
        assertThat(rendered, is(":value:"));
    }

    @Test
    public void testDotForThis() throws Exception {

        Handlebars handlebars = new Handlebars();

        String json = "{'nums':['1','2','3']}".replaceAll("'", "\\\"");
        JsonNode jsonNode = new ObjectMapper().readValue(json, JsonNode.class);

        Context context = Context.newBuilder(jsonNode).resolver(JsonNodeValueResolver.INSTANCE).build();

        Template template = handlebars.compileInline("{{#each nums}}-{{.}}-{{/each}}");
        String rendered = template.apply(context);
        assertThat(rendered, is("-1--2--3-"));

    }
}