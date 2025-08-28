package com.example.ecommerce_springboot.notifications.controller;//package org.example.codetodoapplicationpersonal.notifications.controller;
//
//
//import org.example.codetodoapplicationpersonal.notifications.dtos.EmailRequestDTO;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.web.bind.annotation.*;
//
////Controller when kafka is enabled
/// Controllers are not needed here as it would make it separate microservice not intended here.
//@RestController
//@RequestMapping("/api/notify")
//@RequiredArgsConstructor
//@ConditionalOnProperty(name="use.kafka", havingValue="true")
//public class EmailProducerController {
//
//    private final KafkaTemplate<String, EmailRequestDTO> kafkaTemplate;
//    @Value("${notification.email.topic}")
//    private String topic;
//
//    @PostMapping("/enqueue")
//    public String enqueue(@RequestBody EmailRequestDTO request) {
//        kafkaTemplate.send(topic, request);
//        return "enqueued to kafka";
//    }
//}
//
