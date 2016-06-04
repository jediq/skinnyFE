package com.jediq.skinnyfe;

import org.junit.Test;

/**
 *
 */
public class SkinnyFEMainTest {

    @Test(expected=WrappedException.class)
    public void testExecutesWithMain_WrongConfig() {
        String [] args = new String [] { "" };
        SkinnyFE.main(args);
    }

    @Test
    public void testExecutesWithMain_NoArgs() {
        String [] args = new String [0];
        SkinnyFE.main(args);
    }

    @Test
    public void testExecutesWithMain_CorrectConfig() {
        String [] args = new String [] { "src/test/resources/configForMainTest.json" };
        SkinnyFE.main(args);
    }
}
