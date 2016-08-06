package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.SkinnyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 *
 */
public class FragmentResolver {

    private final Pattern fragmentPattern = Pattern.compile("\\[\\[(.+?)\\]\\]");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Config config;

    private final Cache <String, String> cache;

    public FragmentResolver(Config config) {
        this.config = config;
        cache = new Cache <> (config.getMillisToCacheTemplates());
    }

    public String inlineFragments(String templateText) {
        Matcher matcher = fragmentPattern.matcher(templateText);

        String replacedText = templateText;

        while (matcher.find()) {
            String fragmentId = matcher.group(1);
            String fragment = resolveFragment(fragmentId);
            System.out.println("fragmentId = " + fragmentId);
            System.out.println("fragment = " + fragment);
            replacedText = replacedText.replaceAll("\\[\\["+fragmentId+"\\]\\]", fragment);
        }

        return replacedText;
    }

    public String resolveFragment(String identifier) {
        return cache.item(identifier, () -> resolveFragmentInternal(identifier));
    }

    private String resolveFragmentInternal(String identifier) {
        return fromFile(identifier);
    }

    private String fromFile(String urlPath) {
        String indexedUrlPath = urlPath + (urlPath.endsWith("/") ? "index" : "");

        Path path = Paths.get(config.getDefaultFragments(), indexedUrlPath + ".fragment");
        logger.debug("Looking for fragments from path : " + path);

        if (path.toFile().exists()) {
            SkinnyTemplate template = new SkinnyTemplate();
            template.setFile(path.toFile().getAbsolutePath());
            try {
                return new String(Files.readAllBytes(path));
            } catch (IOException e) {
                logger.info(String.format("Could not find fragment for %s from path: %s", urlPath, path), e);
                return null;
            }
        }
        return null;
    }
}
