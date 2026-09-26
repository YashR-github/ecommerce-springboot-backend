package com.example.ecommerce_springboot.ecommerce.exceptions;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
