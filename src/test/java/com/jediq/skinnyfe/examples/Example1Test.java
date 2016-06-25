package com.jediq.skinnyfe.examples;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 */
public class Example1Test extends BaseExampleTest {

    @Test
    public void test() throws Exception {
        String content = load("/");
        String expectedString = "<h1>Hello World!</h1>";
        assertThat(content, containsString(expectedString));
    }

    @Override
    public int getExampleNumber() {
        return 1;
    }
}
