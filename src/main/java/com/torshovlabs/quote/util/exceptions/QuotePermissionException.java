package com.torshovlabs.quote.util.exceptions;

public class QuotePermissionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public QuotePermissionException(String message) {
        super(message);
    }

    public QuotePermissionException(String message, Throwable cause) {
        super(message, cause);
    }
}