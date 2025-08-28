package com.example.ecommerce_springboot.ecommerce.dto;

import lombok.Data;

@Data
public class ErrorDto {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
