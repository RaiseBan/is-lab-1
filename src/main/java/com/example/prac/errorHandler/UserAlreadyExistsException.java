package com.example.prac.errorHandler;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException (String message) {
        super(message);
    }
}
