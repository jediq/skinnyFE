package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.SkinnyTemplate;
import java.util.Optional;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 */
public class TemplateResolverTest {

    private Config config = createConfig();


    @Test
    public void testResolveTemplate_rootTemplate() throws Exception {
        TemplateResolver templateResolver = new TemplateResolver(config);
        SkinnyTemplate skinnyTemplate = templateResolver.resolveTemplate("http://localhost/");
        assertThat(skinnyTemplate, is(notNullValue()));
    }

    @Test
    public void testResolveTemplate_secondTemplate() throws Exception {
        TemplateResolver templateResolver = new TemplateResolver(config);
        SkinnyTemplate skinnyTemplate = templateResolver.resolveTemplate("http://localhost/second");
        assertThat(skinnyTemplate, is(notNullValue()));
    }

    @Test(expected=IllegalStateException.class)
    public void testResolveTemplate_noTemplate() throws Exception {
        TemplateResolver templateResolver = new TemplateResolver(config);
        SkinnyTemplate template = templateResolver.resolveTemplate("http://localhost/none");
    }

    @Test(expected=IllegalStateException.class)
    public void testResolveTemplate_nullUrl() throws Exception {
        TemplateResolver templateResolver = new TemplateResolver(config);
        SkinnyTemplate template = templateResolver.resolveTemplate(null);
    }

    private Config createConfig() {
        Config config = new Config();
        config.setDefaultTemplates("src/test/resources/basic/templates");
        return config;
    }
}