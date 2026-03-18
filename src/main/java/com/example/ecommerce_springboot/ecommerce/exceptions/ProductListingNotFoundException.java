package com.example.ecommerce_springboot.ecommerce.exceptions;

public class ProductListingNotFoundException extends RuntimeException {
    public ProductListingNotFoundException(String message) {
        super(message);
    }
}
