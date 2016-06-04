package com.jediq.skinnyfe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.JsonNodeValueResolver;
import com.github.jknack.handlebars.Template;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkinnyServlet extends HttpServlet {

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    private Handlebars handlebars = new Handlebars();
    private TemplateResolver templateResolver;
    private ResourceLoader resourceLoader;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        response.setContentType("text/html");

        String url = request.getRequestURL().toString();
        SkinnyTemplate skinnyTemplate = templateResolver.resolveTemplate(url);

        Map<Meta, String> resourceDataMap = resourceLoader.loadResources(skinnyTemplate.metaList);
        JsonNode jsonNode = aggregateData(resourceDataMap);


        Context context = Context.newBuilder(jsonNode)
                .resolver(JsonNodeValueResolver.INSTANCE).build();

        logger.info("aggregated data into : {} ", jsonNode);

        Template template = handlebars.compileInline(skinnyTemplate.content);
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

    public void setTemplateResolver(TemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}