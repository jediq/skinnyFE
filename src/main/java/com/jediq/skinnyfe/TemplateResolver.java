package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.SkinnyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 */
public class TemplateResolver {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Config config;

    public TemplateResolver(Config config) {
        this.config = config;
    }

    public SkinnyTemplate resolveTemplate(String url) throws IOException {

        SkinnyTemplate template = Stream.of(fromConfig(url), fromFile(url))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No template found for : " + url));


        logger.debug("Found template for {} at {}", url, template.getFile());

        template.loadContent(config);

        return template;
    }

    private Optional<SkinnyTemplate> fromFile(String url) throws MalformedURLException {
        if (url==null) {
            return Optional.empty();
        }
        String urlPath = new URL(url).getPath();
        if (urlPath.endsWith("/")) {
            urlPath += "index";
        }
        Path path = Paths.get(config.getDefaultTemplates(), urlPath + ".moustache");
        logger.debug("Looking for template from path : " + path);
        if (path.toFile().exists()) {
            SkinnyTemplate template = new SkinnyTemplate();
            template.setFile(path.toFile().getAbsolutePath());
            return Optional.of(template);
        }
        return Optional.empty();

    }

    private Optional <SkinnyTemplate> fromConfig(String url) {
        for (SkinnyTemplate template : config.getTemplates()) {
            if (template.matches(url)) {
                template.setFile(config.getBaseLocation() + template.getFile());
                return Optional.of(template);
            }
        }
        return Optional.empty();
    }
}
