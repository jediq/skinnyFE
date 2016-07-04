package com.jediq.skinnyfe.enricher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jediq.skinnyfe.WrappedException;
import com.jediq.skinnyfe.config.Config;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class DataEnricher {

    private final Config config;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DataEnricher(Config config) {
        this.config = config;
    }

    public JsonNode enrich(String enricherFile, JsonNode jsonNode, ForceMethods forceMethods) throws IOException {
        String enricher = new String(Files.readAllBytes(Paths.get(config.getBaseLocation(), enricherFile)));

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            // Needs to be a per request instance
            engine.put("force", forceMethods);

            engine.eval(enricher);

            Invocable invocable = (Invocable) engine;

            String result = (String) invocable.invokeFunction("enrich", jsonNode.toString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(result);

            logger.debug("Forced response code? " + forceMethods.getResponseCode().isPresent());

            return node;

        } catch (ScriptException | NoSuchMethodException e) {
            logger.info("Caught exception trying to execute " + enricher, e);
            throw new WrappedException(enricher, e);
        }
    }
}
