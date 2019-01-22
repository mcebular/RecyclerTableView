package com.mc0239.recyclertableview.exception;

public class NotEditableException extends RuntimeException {
    public NotEditableException() {}

    public NotEditableException(String message) {
        super(message);
    }
}