package com.torshovlabs.quote.util.exceptions;

public class NotAllowedToPostException extends RuntimeException {
    public NotAllowedToPostException(String message) {
        super(message);
    }
}