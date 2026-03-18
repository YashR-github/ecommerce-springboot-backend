package com.example.ecommerce_springboot.ecommerce.exceptions;

public class ProductNotFoundException extends RuntimeException{

    //Create an object of ProductNotFound exception class and set the error message in the parameter
    public ProductNotFoundException(String message){
        super(message);
    }
}
