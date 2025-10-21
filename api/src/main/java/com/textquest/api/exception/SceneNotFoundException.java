package com.textquest.api.exception;

public class SceneNotFoundException extends RuntimeException {
    public SceneNotFoundException(String message) {
        super(message);
    }
}
