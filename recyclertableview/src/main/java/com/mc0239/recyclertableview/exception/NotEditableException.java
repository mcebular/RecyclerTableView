package com.mc0239.recyclertableview.exception;

public class NotEditableException extends RuntimeException {
    public NotEditableException() {
        super("This adapter does not have an edittext view id set.");
    }
}