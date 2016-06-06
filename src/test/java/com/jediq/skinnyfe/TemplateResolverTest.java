package com.jediq.skinnyfe;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 */
public class TemplateResolverTest {

    @Test
    public void testResolveTemplate_rootTemplate() throws Exception {
        TemplateResolver templateResolver = new TemplateResolver("src/test/resources/basic/templates");
        SkinnyTemplate skinnyTemplate = templateResolver.resolveTemplate("http://localhost/");
        assertThat(skinnyTemplate, is(notNullValue()));
    }

    @Test
    public void testResolveTemplate_secondTemplate() throws Exception {
        TemplateResolver templateResolver = new TemplateResolver("src/test/resources/basic/templates");
        SkinnyTemplate skinnyTemplate = templateResolver.resolveTemplate("http://localhost/second");
        assertThat(skinnyTemplate, is(notNullValue()));
    }

    @Test(expected=IllegalStateException.class)
    public void testResolveTemplate_noTemplate() throws Exception {
        TemplateResolver templateResolver = new TemplateResolver("src/test/resources/basic/templates");
        templateResolver.resolveTemplate("http://localhost/none");
    }
}