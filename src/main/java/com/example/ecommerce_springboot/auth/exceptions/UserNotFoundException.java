package com.example.ecommerce_springboot.auth.exceptions;



public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
