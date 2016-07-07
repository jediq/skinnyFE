package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.SkinnyTemplate;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class TemplateResolver {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Config config;

    private final Cache <String, SkinnyTemplate> cache;

    private final TemplatePopulater templatePopulater = new TemplatePopulater();

    public TemplateResolver(Config config) {
        this.config = config;
        cache = new Cache <> (config.getMillisToCacheTemplates());
    }

    public SkinnyTemplate resolveTemplate(String url) throws IOException {
        return cache.item(url, () -> resolveTemplateInternal(url));
    }

    private SkinnyTemplate resolveTemplateInternal(String url) {

        if (url == null) {
            return null;
        }
        try {
            String urlPath = new URL(url).getPath();

            Optional <SkinnyTemplate> template = Stream.of(fromConfig(urlPath), fromFile(urlPath))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();

            template.ifPresent(t -> {
                logger.debug("Found template for {} at {}", urlPath, t.getFile());
                t.loadContent();
                templatePopulater.populate(t);
            });

            return template.orElse(null);

        } catch (IOException e) {
            throw new WrappedException("Error finding template for url path :" + url, e);
        }
    }

    private Optional<SkinnyTemplate> fromFile(String urlPath) {
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

    private Optional <SkinnyTemplate> fromConfig(String urlPath) {
        for (SkinnyTemplate template : config.getTemplates()) {
            if (template.matches(urlPath)) {
                template.setFile(config.getBaseLocation() + template.getFile());
                return Optional.of(template);
            }
        }
        return Optional.empty();
    }
}
