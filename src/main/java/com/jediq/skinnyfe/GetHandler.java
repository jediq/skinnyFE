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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class GetHandler extends Handler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GetHandler(Config config) {
        super(config);
    }

    public void doGet(Request request, Response response) throws IOException {

        SkinnyTemplate skinnyTemplate;
        try {
            skinnyTemplate = templateResolver.resolveTemplate(request.getUrl());
        } catch (IllegalStateException e) {
            // we could not find the template
            logger.debug("Could not find template for : " + request.getUrl(), e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        templatePopulater.populate(skinnyTemplate);
        response.setContentType(skinnyTemplate.getContentType());

        try {
            Map<Meta, String> resourceDataMap = resourceInteractor.loadResources(skinnyTemplate.getMetaList(), request);
            JsonNode aggregatedNode = aggregateData(resourceDataMap);

            logger.info("aggregated data into : {} ", aggregatedNode);

            JsonNode enrichedNode;
            if (skinnyTemplate.getEnricher() != null) {
                enrichedNode = enrichData(skinnyTemplate.getEnricher(), aggregatedNode);
                logger.info("enriched data into : {} ", enrichedNode);
            } else {
                enrichedNode = aggregatedNode;
            }


            Context context = Context.newBuilder(enrichedNode)
                    .resolver(JsonNodeValueResolver.INSTANCE).build();

            Template template = handlebars.compileInline(skinnyTemplate.getContent());
            String rendered = template.apply(context);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(rendered);
        } catch(BadResponseException e) {
            logger.debug("Resource returned a bad response code : " + e.getStatus(), e);
            response.setStatus(e.getStatus());
        }

    }

    private JsonNode aggregateData(Map<Meta, String> resourceDataMap) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode rootNode = mapper.createObjectNode();
        for (Map.Entry<Meta, String> entry : resourceDataMap.entrySet()) {
            try {
                JsonNode node = mapper.readTree(entry.getValue());
                rootNode.put(entry.getKey().getProperty(), node);
                logger.info("Put {} into property {}", node, entry.getKey().getProperty());
            } catch (IOException e) {
                logger.info("Caught exception processing : " + entry.getValue(), e);
            }
        }
        return rootNode;
    }

    private JsonNode enrichData(String enricherFile, JsonNode jsonNode) throws IOException {
        String enricher = new String(Files.readAllBytes(Paths.get(enricherFile)));

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval(enricher);

            Invocable invocable = (Invocable) engine;

            String result = (String) invocable.invokeFunction("enrich", jsonNode.toString());

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(result);

        } catch (ScriptException | NoSuchMethodException e) {
            logger.info("Caught exception trying to execute " + enricher, e);
            throw new WrappedException(e);
        }
    }
}
