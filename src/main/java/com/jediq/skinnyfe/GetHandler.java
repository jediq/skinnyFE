package com.jediq.skinnyfe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.JsonNodeValueResolver;
import com.github.jknack.handlebars.Template;
import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.Meta;
import com.jediq.skinnyfe.config.SkinnyTemplate;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetHandler extends Handler {

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    public GetHandler(Config config) {
        super(config);
    }

    public void doGet(Request request, HttpServletResponse response) throws IOException {

        SkinnyTemplate skinnyTemplate;
        try {
            String url = request.url.toString();
            skinnyTemplate = templateResolver.resolveTemplate(url);
        } catch (IllegalStateException e) {
            // we could not find the template
            logger.debug("Could not find template for : " + request.url, e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        templatePopulater.populate(skinnyTemplate);
        response.setContentType(skinnyTemplate.getContentType());

        Map<Meta, String> resourceDataMap = resourceInteractor.loadResources(skinnyTemplate.getMetaList());
        JsonNode jsonNode = aggregateData(resourceDataMap);

        Context context = Context.newBuilder(jsonNode)
                .resolver(JsonNodeValueResolver.INSTANCE).build();

        logger.debug("aggregated data into : {} ", jsonNode);

        Template template = handlebars.compileInline(skinnyTemplate.getContent());
        String rendered = template.apply(context);


        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(rendered);
    }

    private JsonNode aggregateData(Map<Meta, String> resourceDataMap) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode rootNode = mapper.createObjectNode();
        for (Meta meta : resourceDataMap.keySet()) {
            try {
                JsonNode node = mapper.readTree(resourceDataMap.get(meta));
                rootNode.put(meta.getProperty(), node);
                logger.info("Put {} into property {}", node, meta.getProperty());
            } catch (IOException e) {
                logger.info("Caught exception processing : " + resourceDataMap.get(meta), e);
            }
        }
        return rootNode;
    }
}
