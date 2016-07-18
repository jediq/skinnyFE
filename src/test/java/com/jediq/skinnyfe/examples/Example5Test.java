package com.jediq.skinnyfe.examples;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 */
public class Example5Test extends BaseExampleTest {

    @Test
    public void test() throws Exception {
        String content = load("/");
        String expectedString = "<h2>MY FIRST POST <small> by </small> FRED JONES</h2>";
        assertThat(content, containsString(expectedString));
    }


    @Override
    public int getExampleNumber() {
        return 5;
    }
}
