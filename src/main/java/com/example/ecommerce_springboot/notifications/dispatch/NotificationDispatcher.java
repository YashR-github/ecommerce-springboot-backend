package com.example.ecommerce_springboot.notifications.dispatch;


import com.example.ecommerce_springboot.notifications.dtos.EmailRequestDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final KafkaTemplate<String, EmailRequestDTO> kafkaTemplate;
    @Value("${notification.email.topic}")
    private String topic;

    // This bean will be used only when use.kafka=true because KafkaTemplate bean defined only then
    public void dispatchToKafka(EmailRequestDTO request) {
        kafkaTemplate.send(topic, request);
    }
}

