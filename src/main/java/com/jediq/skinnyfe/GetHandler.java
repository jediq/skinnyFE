package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
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
import com.jediq.skinnyfe.resource.ResourceResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetHandler extends Handler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DataEnricher dataEnricher;

    private final ObjectMapper mapper = new ObjectMapper();

    private final ResponseParser responseParser = new ResponseParser();

    public GetHandler(Config config, MetricRegistry metrics) {
        super(config, metrics);
        dataEnricher = new DataEnricher(config);
    }

    public boolean doGet(Request request, Response response) throws IOException {

        SkinnyTemplate skinnyTemplate = templateResolver.resolveTemplate(new URL(request.getUrl()).getPath());
        if (skinnyTemplate == null) {
            // we could not find the template
            logger.debug("Could not find template for : " + request.getUrl());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }

        response.setContentType(skinnyTemplate.getContentType());

        Map<Meta, ResourceResponse> resourceDataMap = resourceReader.loadResources(skinnyTemplate.getMetaList(), request);
        JsonNode aggregatedNode = aggregateData(resourceDataMap);

        logger.debug("aggregated data into : {} ", aggregatedNode);

        JsonNode enrichedNode;

        logger.debug("enriching data with {} enrichers", skinnyTemplate.getEnrichers().size());
        ForceMethods forceMethods = null;
        if (!skinnyTemplate.getEnrichers().isEmpty()) {
            forceMethods = new ForceMethods();
            enrichedNode = dataEnricher.enrich(skinnyTemplate.getEnrichers(), aggregatedNode, forceMethods);
            logger.debug("enriched data into : {} ", enrichedNode);
        } else {
            enrichedNode = aggregatedNode;
        }

        if (forceMethods != null && forceMethods.getTemplate().isPresent()) {
            skinnyTemplate = templateResolver.resolveTemplate(forceMethods.getTemplate().get());
            if (skinnyTemplate == null) {
                // we could not find the template
                logger.debug("Could not find template for : " + request.getUrl());
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return false;
            }
        }


        Context context = Context.newBuilder(enrichedNode)
                .resolver(JsonNodeValueResolver.INSTANCE).build();

        String rendered = handlebarsCompiler.compile(skinnyTemplate.getContent(), context, 1);

        int status = calculateStatus(HttpServletResponse.SC_OK, forceMethods);
        response.setStatus(status);
        if (status == HttpServletResponse.SC_OK) {
            response.getWriter().println(rendered);
            return true;
        }
        return false;
    }

    private int calculateStatus(int defaultStatus, ForceMethods forceMethods) {
        if (forceMethods == null) {
            return defaultStatus;
        }
        return forceMethods.getResponseCode().orElse(defaultStatus);
    }

    private JsonNode aggregateData(Map<Meta, ResourceResponse> resourceDataMap) {
        ObjectNode rootNode = mapper.createObjectNode();
        for (Map.Entry<Meta, ResourceResponse> entry : resourceDataMap.entrySet()) {
            ObjectNode node = responseParser.parseJsonFromResponse(entry.getValue());
            ObjectNode metaNode = node.putObject("_meta");
            metaNode.put("code", entry.getValue().code);
            metaNode.put("reason", entry.getValue().reason);
            metaNode.put("url", entry.getValue().url);
            rootNode.put(entry.getKey().getProperty(), node);
            logger.debug("Put {} into property {}", node, entry.getKey().getProperty());
        }
        return rootNode;
    }

}
