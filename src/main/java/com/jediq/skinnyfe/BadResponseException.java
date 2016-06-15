package com.jediq.skinnyfe;

public class BadResponseException extends RuntimeException {

    private final int status;

    public BadResponseException(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
