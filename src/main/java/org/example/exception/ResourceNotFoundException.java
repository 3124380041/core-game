package org.example.exception;

/**
 * Exception cho resource không tìm thấy.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

