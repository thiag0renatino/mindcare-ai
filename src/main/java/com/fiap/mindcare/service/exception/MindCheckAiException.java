package com.fiap.mindcare.service.exception;

public class MindCheckAiException extends RuntimeException {

    public MindCheckAiException(String message) {
        super(message);
    }

    public MindCheckAiException(String message, Throwable cause) {
        super(message, cause);
    }
}
