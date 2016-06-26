package com.jediq.skinnyfe.examples;

import java.util.HashMap;
import java.util.Map;
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
        String [] expectedStrings = new String [] {
                "<h2><a href=\"post?id=1\">My first post <small> by </small> Fred Jones</a></h2>",
                "<input type=\"text\" placeholder=\"Title\" name=\"posts.title\">",
                "<input type=\"text\" placeholder=\"Author\" name=\"posts.author\">"
        };
        for (String expectedString : expectedStrings) {
            assertThat(content, containsString(expectedString));
        }

        Map<String, String> map = new HashMap<>();
        map.put("posts.title", "test_title");
        map.put("posts.author", "test_author");
        String postResponse = post("/", map);
        String postedExpectedString = "<h2><a href=\"post?id=2\">test_title <small> by </small> test_author</a></h2>";
        assertThat(postResponse, containsString(postedExpectedString));
    }


    @Override
    public int getExampleNumber() {
        return 4;
    }
}
