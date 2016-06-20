package com.jediq.skinnyfe.config;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigTest {

    @Test(expected=IllegalStateException.class)
    public void testLoadConfigWhenNoPortIsSet() throws Exception {
        Config.load("src/test/resources/configWithNoPort.json");
    }

    @Test
    public void testLoadConfigWhenPortIsSet() throws Exception {
        Config.load("src/test/resources/configForMainTest.json");
    }
}