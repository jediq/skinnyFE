package com.jediq.skinnyfe.enricher;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ForceMethods {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Integer responseCode;
    private String template;

    public void template(String template) {
        logger.debug("Forcing tempalte to : {}", template);
        this.template = template;
    }

    public void responseCode(int responseCode) {
        logger.debug("Forcing response code to : {}", responseCode);
        this.responseCode = responseCode;
    }

    public Optional<Integer> getResponseCode() {
        return Optional.ofNullable(responseCode);
    }

    public Optional<String> getTemplate() {
        return Optional.ofNullable(template);
    }


}
