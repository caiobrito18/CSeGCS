package com.mylibrary;

public class LivroException extends RuntimeException {
    
    public LivroException(String message) {
        super(message);
    }
    
    public LivroException(String message, Throwable cause) {
        super(message, cause);
    }
}
