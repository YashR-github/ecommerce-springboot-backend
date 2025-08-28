package com.example.ecommerce_springboot.notifications.service;


import com.example.ecommerce_springboot.notifications.dtos.EmailRequestDTO;

public interface EmailService {
    void send(EmailRequestDTO request) throws Exception;
}
