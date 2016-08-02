package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.Meta;
import com.jediq.skinnyfe.config.SkinnyTemplate;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is implemented as POST/REDIRECT/GET.
 *
 * Any request sent to a page with a POST method will hit this, we pull out the form
 * data and send that onto the Resources they reference before doing a redirect.
 *
 * @link https://en.wikipedia.org/wiki/Post/Redirect/Get
 *
 *
 */
public class PostHandler extends Handler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DotNotationTransformer dotNotationTransformer;

    public PostHandler(Config config, MetricRegistry metrics) {
        super(config, metrics);
        dotNotationTransformer = new DotNotationTransformer();
    }

    public void doPost(Request request, Response response) throws IOException {


        SkinnyTemplate skinnyTemplate = templateResolver.resolveTemplate(new URL(request.getUrl()).getPath());
        if (skinnyTemplate == null) {
            // we could not find the template
            logger.debug("Could not find template for : " + request.getUrl());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            ObjectNode rootNode = dotNotationTransformer.toJson(request.getParams());

            Map <Meta, String> metaStringMap = new HashMap<>();
            skinnyTemplate.getMetaList().forEach(meta -> {
                if (rootNode.has(meta.getProperty())) {
                    metaStringMap.put(meta, rootNode.get(meta.getProperty()).toString());
                }
            });

            logger.debug("Resources to write to : {}", metaStringMap.size());
            metaStringMap.keySet().forEach(meta ->
                logger.debug("{} : {}", meta, metaStringMap.get(meta))
            );

            resourceInteractor.saveResources(metaStringMap, request);

            logger.info("POSTed, about to redirect to GET");
            response.sendRedirect(request.getUrl());
        } catch (IOException | NullPointerException e) {
            logger.info("Exception trying to redirect to : " + request.getUrl(), e);
            response.setStatus(500);
        }
    }
}
