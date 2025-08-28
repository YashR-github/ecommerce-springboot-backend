package com.example.ecommerce_springboot.auth.exceptions;



public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
