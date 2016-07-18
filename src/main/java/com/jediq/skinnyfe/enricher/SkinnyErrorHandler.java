package com.jediq.skinnyfe.enricher;

import com.jediq.skinnyfe.config.Config;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkinnyErrorHandler extends ErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<Integer, String> codeMappings = new HashMap<>();

    public SkinnyErrorHandler(Config config) {
        for (Map.Entry<Integer, String> page : config.getErrorPages().entrySet()) {
            addCodeMapping(page.getKey(), page.getValue());
        }
    }

    public void addCodeMapping(Integer key, String value) {
        codeMappings.put(key, value);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.debug("error status is = {}", response.getStatus());
        if (codeMappings.containsKey(response.getStatus())) {
            Path path = Paths.get(codeMappings.get(response.getStatus()));
            logger.debug("loading error page from = {}", path);
            byte[] allBytes = Files.readAllBytes(path);
            response.getOutputStream().write(allBytes);
        }
    }
}
