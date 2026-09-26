package com.example.ecommerce_springboot.ecommerce.exceptions;

public class SellerProductAlreadyExistException extends ResourceAlreadyExistException {
    public SellerProductAlreadyExistException(String message) {
        super(message);
    }
}
