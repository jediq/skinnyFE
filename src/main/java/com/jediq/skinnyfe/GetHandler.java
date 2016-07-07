package com.jediq.skinnyfe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.JsonNodeValueResolver;
import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.Meta;
import com.jediq.skinnyfe.config.SkinnyTemplate;
import com.jediq.skinnyfe.enricher.DataEnricher;
import com.jediq.skinnyfe.enricher.ForceMethods;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetHandler extends Handler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DataEnricher dataEnricher;

    public GetHandler(Config config) {
        super(config);
        dataEnricher = new DataEnricher(config);
    }

    public void doGet(Request request, Response response) throws IOException {

        SkinnyTemplate skinnyTemplate = templateResolver.resolveTemplate(request.getUrl());
        if (skinnyTemplate == null) {
            // we could not find the template
            logger.debug("Could not find template for : " + request.getUrl());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(skinnyTemplate.getContentType());

        Map<Meta, ResourceResponse> resourceDataMap = resourceInteractor.loadResources(skinnyTemplate.getMetaList(), request);
        JsonNode aggregatedNode = aggregateData(resourceDataMap);

        logger.debug("aggregated data into : {} ", aggregatedNode);

        JsonNode enrichedNode;

        logger.debug("enriching data? : {} ", skinnyTemplate.getEnricher());
        ForceMethods forceMethods = null;
        if (skinnyTemplate.getEnricher() != null) {
            forceMethods = new ForceMethods();
            enrichedNode = dataEnricher.enrich(skinnyTemplate.getEnricher(), aggregatedNode, forceMethods);
            logger.debug("enriched data into : {} ", enrichedNode);
        } else {
            enrichedNode = aggregatedNode;
        }

        if (forceMethods != null && forceMethods.getTemplate().isPresent()) {
            skinnyTemplate = templateResolver.resolveTemplate(request.getUrl());
            if (skinnyTemplate == null) {
                // we could not find the template
                logger.debug("Could not find template for : " + request.getUrl());
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }


        Context context = Context.newBuilder(enrichedNode)
                .resolver(JsonNodeValueResolver.INSTANCE).build();

        String rendered = handlebarsCompiler.compile(skinnyTemplate.getContent(), context, 1);

        response.setStatus(calculateStatus(HttpServletResponse.SC_OK, forceMethods));
        response.getWriter().println(rendered);
    }

    private int calculateStatus(int defaultStatus, ForceMethods forceMethods) {
        if (forceMethods == null) {
            return defaultStatus;
        }
        return forceMethods.getResponseCode().orElse(defaultStatus);
    }

    private JsonNode aggregateData(Map<Meta, ResourceResponse> resourceDataMap) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode rootNode = mapper.createObjectNode();
        for (Map.Entry<Meta, ResourceResponse> entry : resourceDataMap.entrySet()) {
            try {
                ObjectNode node = mapper.createObjectNode();
                if (!entry.getValue().content.isEmpty()) {
                    JsonNode jsonNode = mapper.readTree(entry.getValue().content);
                    if (jsonNode instanceof ObjectNode) {
                        node = (ObjectNode) jsonNode;
                    } else {
                        node.put("array", jsonNode);
                    }
                }
                ObjectNode metaNode = node.putObject("_meta");
                metaNode.put("code", entry.getValue().code);
                metaNode.put("reason", entry.getValue().reason);
                rootNode.put(entry.getKey().getProperty(), node);
                logger.debug("Put {} into property {}", node, entry.getKey().getProperty());
            } catch (IOException e) {
                logger.info("Caught exception processing : " + entry.getValue(), e);
            }
        }
        return rootNode;
    }

}
