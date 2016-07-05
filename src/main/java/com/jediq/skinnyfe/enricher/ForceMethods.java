package com.jediq.skinnyfe.enricher;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ForceMethods {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ThreadLocal<Integer> responseCodeThreadLocal = new ThreadLocal<>();

    public void responseCode(int code) {
        logger.debug("Forcing response code to : " + code);
        responseCodeThreadLocal.set(code);
    }

    public Optional<Integer> getResponseCode() {
        return Optional.ofNullable(responseCodeThreadLocal.get());
    }
}