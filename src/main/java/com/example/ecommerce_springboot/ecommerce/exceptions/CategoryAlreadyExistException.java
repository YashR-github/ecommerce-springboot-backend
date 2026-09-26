package com.example.ecommerce_springboot.ecommerce.exceptions;

public class CategoryAlreadyExistException extends ResourceAlreadyExistException {
    public CategoryAlreadyExistException(String message) {
        super(message);
    }
}
