package com.jediq.skinnyfe.examples;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 */
public class Example3Test extends BaseExampleTest {

    @Test
    public void test() throws Exception {
        String content = load("/");
        String firstExpectedString = "<h2><a href=\"post?id=1\">My first post <small> by </small> Fred Jones</a></h2>";
        assertThat(content, containsString(firstExpectedString));

        String content2 = load("/post?id=1");
        String secondExpectedString = "<p>This is a very very verbose post</p>";
        assertThat(content2, containsString(secondExpectedString));
    }

    @Override
    public int getExampleNumber() {
        return 3;
    }
}
