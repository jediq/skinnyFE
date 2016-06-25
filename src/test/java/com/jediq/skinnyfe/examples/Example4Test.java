package com.jediq.skinnyfe.examples;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 */
public class Example4Test extends BaseExampleTest {

    @Test
    public void test() throws Exception {
        String content = load("/");
        String expectedString = "<h2><a href=\"post?id=1\">My first post <small> by </small> Fred Jones</a></h2>";
        assertThat(content, containsString(expectedString));
    }

    @Override
    public int getExampleNumber() {
        return 4;
    }
}
