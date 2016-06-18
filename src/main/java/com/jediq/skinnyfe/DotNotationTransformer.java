package com.jediq.skinnyfe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

public class DotNotationTransformer {

    public ObjectNode toJson(Map<String, String> values) {

        ObjectNode rootNode = new ObjectMapper().createObjectNode();

        values.keySet().stream().forEach(key -> add(rootNode, key, values.get(key)));


        return rootNode;
    }

    private void add(ObjectNode node, String key, String value) {

        if (key.contains(".")) {
            int firstDotPos = key.indexOf('.');
            String name = key.substring(0, firstDotPos);
            String leftover = key.substring(firstDotPos + 1, key.length());
            ObjectNode child;
            if (node.has(name)) {
                child = (ObjectNode) node.get(name);
            } else {
                child = node.putObject(name);
            }
            add(child, leftover, value);
        } else {
            node.put(key, value);
        }
    }


}
