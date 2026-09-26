package com.example.ecommerce_springboot.ecommerce.exceptions;

public class CartItemNotFoundException extends ResourceNotFoundException {
    public CartItemNotFoundException(String message) {
        super(message);
    }
}
