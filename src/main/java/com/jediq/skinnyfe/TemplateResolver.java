package com.jediq.skinnyfe;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
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

        if (!filePath.toFile().exists()) {
            return skinnyTemplate;
        }

        skinnyTemplate.content = new String(Files.readAllBytes(filePath));

        Document document = Jsoup.parse(skinnyTemplate.content);
        for (Element element : document.head().getElementsByTag("meta")) {
            if (hasResourceAttribute(element)) {
                skinnyTemplate.metaList.add(convertElementToMeta(element));
            }
        }
        return skinnyTemplate;
    }

    private boolean hasResourceAttribute(Element e) {
        return e.hasAttr("resource");
    }

    private Meta convertElementToMeta(Element element) {
        Meta meta = new Meta();
        meta.setProperty(element.attr("property"));
        meta.setIdentifier(element.attr("identifier"));
        meta.setResource(element.attr("resource"));
        return meta;
    }
}
