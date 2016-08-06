package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.Meta;
import com.jediq.skinnyfe.config.SkinnyTemplate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class TemplatePopulater {

    private final FragmentResolver fragmentResolver;

    public TemplatePopulater(Config config) {
        this.fragmentResolver = new FragmentResolver(config);
    }

    public void populate(SkinnyTemplate skinnyTemplate) {

        String content = skinnyTemplate.getContent();
        String contentWithFragments = fragmentResolver.inlineFragments(content);
        skinnyTemplate.setContent(contentWithFragments);

        Document document = Jsoup.parse(content);
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
