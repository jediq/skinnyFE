package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class FragmentResolverTest {

    @Test
    public void testInline() throws Exception {
        Config config = new Config();
        config.setDefaultFragments("src/test/resources/fragmentTests");
        FragmentResolver fragmentResolver = new FragmentResolver(config);

        String templateText = load("src/test/resources/fragmentTests/FragmentTestBefore.txt");
        String expectedText = load("src/test/resources/fragmentTests/FragmentTestExpected.txt");

        String actualText = fragmentResolver.inlineFragments(templateText);
        assertThat(actualText, is(expectedText));
    }

    private String load(String textFile) throws Exception  {
        return new String(Files.readAllBytes(Paths.get(textFile)));
    }
}