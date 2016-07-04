package com.jediq.skinnyfe.config;

import com.jediq.skinnyfe.WrappedException;
import java.io.File;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 */
public class SkinnyTemplateTest {

    @Test(expected=IllegalStateException.class)
    public void testLoadContent_emptyContent() throws Exception {
        SkinnyTemplate skinnyTemplate = new SkinnyTemplate();
        skinnyTemplate.loadContent();
    }

    @Test(expected= WrappedException.class)
    public void testLoadContent_fileIsAFolder() throws Exception {
        SkinnyTemplate skinnyTemplate = new SkinnyTemplate();
        skinnyTemplate.setFile(new File(".").getAbsolutePath());
        skinnyTemplate.loadContent();

    }
}