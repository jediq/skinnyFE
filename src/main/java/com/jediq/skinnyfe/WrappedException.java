package com.jediq.skinnyfe;

/**
 *
 */
public class WrappedException extends RuntimeException {

    public WrappedException(Exception e) {
        super(e);
    }
}
