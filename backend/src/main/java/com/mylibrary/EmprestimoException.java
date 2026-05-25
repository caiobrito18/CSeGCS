package com.mylibrary;

public class EmprestimoException extends RuntimeException {

    public EmprestimoException(String message) {
        super(message);
    }

    public EmprestimoException(String message, Throwable cause) {
        super(message, cause);
    }
}
