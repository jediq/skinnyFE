package com.jediq.skinnyfe.examples;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 */
public class Example2Test extends BaseExampleTest {

    @Test
    public void test() throws Exception {
        String content = load("/");
        String expectedString = "<h2>My first post <small> by </small> Fred Jones</h2>";
        assertThat(content, containsString(expectedString));
    }

    @Override
    public int getExampleNumber() {
        return 2;
    }
}
