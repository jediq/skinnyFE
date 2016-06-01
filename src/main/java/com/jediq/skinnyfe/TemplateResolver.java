package com.jediq.skinnyfe;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public class TemplateResolver {

    private String templatesLocation;

    public TemplateResolver(String templatesLocation) {
        this.templatesLocation = templatesLocation;
    }

    public SkinnyTemplate resolveTemplate(String url) throws IOException {
        String path = new URL(url).getPath();
        if (path.endsWith("/")) {
            path += "index";
        }
        Path filePath = Paths.get(templatesLocation, path + ".moustache");
        SkinnyTemplate skinnyTemplate = new SkinnyTemplate();
        skinnyTemplate.content = new String(Files.readAllBytes(filePath));
        return skinnyTemplate;
    }
}
