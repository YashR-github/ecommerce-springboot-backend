package com.example.ecommerce_springboot.ecommerce.exceptions;

public class InventoryOutOfStockException extends RuntimeException {
    public InventoryOutOfStockException(String message) {
        super(message);
    }
}
