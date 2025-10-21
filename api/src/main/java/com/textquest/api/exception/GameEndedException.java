package com.textquest.api.exception;

public class GameEndedException extends RuntimeException {
    public GameEndedException(String message) {
        super(message);
    }
}
