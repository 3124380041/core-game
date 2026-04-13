package org.example.exception;

/**
 * Exception chung cho game logic.
 */
public class GameException extends RuntimeException {
    
    public GameException(String message) {
        super(message);
    }
}

