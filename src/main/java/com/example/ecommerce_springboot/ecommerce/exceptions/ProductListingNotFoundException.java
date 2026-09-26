package com.example.ecommerce_springboot.ecommerce.exceptions;

public class ProductListingNotFoundException extends ResourceNotFoundException {
    public ProductListingNotFoundException(String message) {
        super(message);
    }
}
