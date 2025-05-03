package com.torshovlabs.quote.util.exceptions;

public class QuoteAlreadyExistsException extends RuntimeException {
    public QuoteAlreadyExistsException(String message) {
        super(message);
    }
}