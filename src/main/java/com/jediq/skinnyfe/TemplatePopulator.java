package com.jediq.skinnyfe;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 */
public class TemplatePopulator {

    public void populate(SkinnyTemplate skinnyTemplate) {

        Document document = Jsoup.parse(skinnyTemplate.getContent());
        for (Element element : document.head().getElementsByTag("meta")) {
            if (hasResourceAttribute(element)) {
                skinnyTemplate.getMetaList().add(convertElementToMeta(element));
            }
        }
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
