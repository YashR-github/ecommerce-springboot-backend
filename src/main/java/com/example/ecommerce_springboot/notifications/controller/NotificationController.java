package com.example.ecommerce_springboot.notifications.controller;//package org.example.codetodoapplicationpersonal.notifications.controller;
//
//
//import org.example.codetodoapplicationpersonal.notifications.dtos.EmailRequestDTO;
//import org.example.codetodoapplicationpersonal.notifications.dispatch.DirectNotificationDispatcher;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.web.bind.annotation.*;
//
//
////Controller to send email notifications directly via sendgrid/javamailsender when kafka is disabled
/// Controllers are not needed here as it would make it separate microservice not intended here.
//@RestController
//@RequestMapping("/api/notification")
//@RequiredArgsConstructor
//@ConditionalOnProperty(name="use.kafka", havingValue="false", matchIfMissing=true)
//public class NotificationController {
//
//    private final DirectNotificationDispatcher dispatcher;
//
//    @PostMapping("/send")
//    public String send(@RequestBody EmailRequestDTO req) throws Exception {
//        dispatcher.dispatchDirect(req);
//        return "sent";
//    }
//}
