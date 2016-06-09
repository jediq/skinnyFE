package com.jediq.skinnyfe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

public class DotNotationTransformer {

    public String toJson(Map<String, String> values) {

        ObjectNode rootNode = new ObjectMapper().createObjectNode();



        return rootNode.toString();
    }


}
