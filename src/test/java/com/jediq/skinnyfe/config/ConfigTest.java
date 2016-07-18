package com.jediq.skinnyfe.config;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class ConfigTest {

    @Test(expected=IllegalStateException.class)
    public void testLoadConfigWhenNoPortIsSet() throws Exception {
        Config.load("src/test/resources/configWithNoPort.json");
    }

    @Test
    public void testLoadConfigWhenPortIsSet() throws Exception {
        Config.load("src/test/resources/configForMainTest.json");
    }

    @Test
    public void testGetAssetsFolder_isNullWhenNotSet() {
        Config config = new Config();
        config.setBaseLocation("bbbb");
        assertThat(config.getAssetsFolder(), is(nullValue()));
    }

    @Test
    public void testGetAssetsFolder_conflatesBaseLocation() {
        Config config = new Config();
        config.setBaseLocation("bbbb");
        config.setAssetsFolder("cccc");
        assertThat(config.getAssetsFolder(), is("bbbbcccc"));
    }
}