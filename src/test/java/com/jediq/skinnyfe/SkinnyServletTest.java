package com.jediq.skinnyfe;

import org.junit.Test;

/**
 */
public class SkinnyServletTest {

    @Test(expected=NullPointerException.class)
    public void testCallGetWhenConfigIsNotSet() throws Exception {
        SkinnyServlet skinnyServlet = new SkinnyServlet();
        skinnyServlet.doGet(null, null);
    }

    @Test(expected=NullPointerException.class)
    public void testCallPostWhenConfigIsNotSet() throws Exception {
        SkinnyServlet skinnyServlet = new SkinnyServlet();
        skinnyServlet.doPost(null, null);
    }

}