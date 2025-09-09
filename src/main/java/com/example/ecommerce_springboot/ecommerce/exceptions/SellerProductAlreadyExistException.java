package com.example.ecommerce_springboot.ecommerce.exceptions;

public class SellerProductAlreadyExistException extends RuntimeException {
    public SellerProductAlreadyExistException(String message) {
        super(message);
    }
}
