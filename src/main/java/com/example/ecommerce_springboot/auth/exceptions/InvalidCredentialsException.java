package com.example.ecommerce_springboot.auth.exceptions;



public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
