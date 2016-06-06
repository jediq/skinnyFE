package com.jediq.skinnyfe;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class TemplateResolver {

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

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

        logger.debug("Looking for template for {} at {}", url, filePath);

        if (!filePath.toFile().exists()) {
            String message = String.format("Could not find template for %s at %s", url, filePath);
            throw new IllegalStateException(message);
        }

        SkinnyTemplate skinnyTemplate = new SkinnyTemplate();
        skinnyTemplate.setContent(new String(Files.readAllBytes(filePath)));

        Document document = Jsoup.parse(skinnyTemplate.getContent());
        for (Element element : document.head().getElementsByTag("meta")) {
            if (hasResourceAttribute(element)) {
                skinnyTemplate.getMetaList().add(convertElementToMeta(element));
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
