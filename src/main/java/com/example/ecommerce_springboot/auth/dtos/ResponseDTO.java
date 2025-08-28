package com.example.ecommerce_springboot.auth.dtos;


import lombok.Data;


public class ResponseDTO<T> {
    public String message;
    public T data;

    public ResponseDTO(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
