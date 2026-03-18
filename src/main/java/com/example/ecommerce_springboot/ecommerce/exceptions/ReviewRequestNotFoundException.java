package com.example.ecommerce_springboot.ecommerce.exceptions;

public class ReviewRequestNotFoundException extends RuntimeException {
    public ReviewRequestNotFoundException(String message) {
        super(message);
    }
}
