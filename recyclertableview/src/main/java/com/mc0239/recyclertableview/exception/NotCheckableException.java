package com.mc0239.recyclertableview.exception;

public class NotCheckableException extends RuntimeException {
    public NotCheckableException() {
        super("This adapter does not have a checkbox view id set.");
    }
}