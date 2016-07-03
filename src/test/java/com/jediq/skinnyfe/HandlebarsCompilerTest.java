package com.jediq.skinnyfe;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import junitx.util.PrivateAccessor;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class HandlebarsCompilerTest {

    @Test
    public void testPlainCompile() {
        HandlebarsCompiler handlebarsCompiler = new HandlebarsCompiler();
        Map<String, Object> map = new HashMap<>();
        map.put("first", "1st");
        map.put("second", "2nd");
        map.put("third", "3rd");
        String compiled = handlebarsCompiler.compile("{{first}}-{{second}}-{{third}}", map);
        assertThat(compiled, is("1st-2nd-3rd"));
    }

    @Test
    public void test1Iteration() {
        HandlebarsCompiler handlebarsCompiler = new HandlebarsCompiler();
        Map<String, Object> map = new HashMap<>();
        map.put("first", "1st");
        map.put("second", "{{secnd}}");
        map.put("third", "3rd");
        map.put("secnd", "SECOND");
        String compiled = handlebarsCompiler.compile("{{first}}-{{second}}-{{third}}", map, 1);
        assertThat(compiled, is("1st-{{secnd}}-3rd"));
    }

    @Test
    public void test2Iterations() {
        HandlebarsCompiler handlebarsCompiler = new HandlebarsCompiler();
        Map<String, Object> map = new HashMap<>();
        map.put("first", "1st");
        map.put("second", "{{secnd}}");
        map.put("third", "3rd");
        map.put("secnd", "SECOND");
        String compiled = handlebarsCompiler.compile("{{first}}-{{second}}-{{third}}", map, 2);
        assertThat(compiled, is("1st-SECOND-3rd"));
    }

    @Test(expected=WrappedException.class)
    public void testFakeIOExceptionWithMap() throws Exception {
        String template = "{{first}}-{{second}}-{{third}}";
        HandlebarsCompiler handlebarsCompiler = new HandlebarsCompiler();
        Handlebars handlebars = mock(Handlebars.class);
        when(handlebars.compileInline(template)).thenThrow(new IOException());
        PrivateAccessor.setField(handlebarsCompiler, "handlebars", handlebars);
        handlebarsCompiler.compile(template, new HashMap<>());
    }

    @Test(expected=WrappedException.class)
    public void testFakeIOExceptionWithContext() throws Exception {
        String template = "{{first}}-{{second}}-{{third}}";
        HandlebarsCompiler handlebarsCompiler = new HandlebarsCompiler();
        Handlebars handlebars = mock(Handlebars.class);
        when(handlebars.compileInline(template)).thenThrow(new IOException());
        PrivateAccessor.setField(handlebarsCompiler, "handlebars", handlebars);
        handlebarsCompiler.compile(template,Context.newContext(""));
    }



}