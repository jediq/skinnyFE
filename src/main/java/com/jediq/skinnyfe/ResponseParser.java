package com.jediq.skinnyfe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jediq.skinnyfe.resource.ResourceResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseParser {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper mapper = new ObjectMapper();

    public ObjectNode parseJsonFromResponse(ResourceResponse resourceResponse) {
        ObjectNode node = mapper.createObjectNode();
        if (!resourceResponse.content.isEmpty()) {
            try {
                JsonNode jsonNode = mapper.readTree(resourceResponse.content);
                if (jsonNode instanceof ObjectNode) {
                    node = (ObjectNode) jsonNode;
                } else {
                    node.put("array", jsonNode);
                }
            } catch (IOException e) {
                logger.debug("Response didn't give valid json : " + resourceResponse.content, e);
            }
        }
        return node;
    }

}
