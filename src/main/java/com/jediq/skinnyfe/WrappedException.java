package com.jediq.skinnyfe;

/**
 *
 */
public class WrappedException extends RuntimeException {

    public WrappedException(String s, Exception e) {
        super(s, e);
    }

    public WrappedException(Exception e) {
        super(e);
    }
}
