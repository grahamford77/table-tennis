package com.tabletennis.exception;

/**
 * Exception thrown when email template loading fails
 */
public class EmailTemplateException extends RuntimeException {
    public EmailTemplateException(String message, Throwable cause) {
        super(message, cause);
    }
}
