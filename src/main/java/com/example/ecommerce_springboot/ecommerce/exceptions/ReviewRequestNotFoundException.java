package com.example.ecommerce_springboot.ecommerce.exceptions;

public class ReviewRequestNotFoundException extends ResourceNotFoundException {
    public ReviewRequestNotFoundException(String message) {
        super(message);
    }
}
