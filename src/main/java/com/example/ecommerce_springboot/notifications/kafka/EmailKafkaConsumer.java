package com.example.ecommerce_springboot.notifications.kafka;


import com.example.ecommerce_springboot.notifications.dtos.EmailRequestDTO;
import com.example.ecommerce_springboot.notifications.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name="use.kafka", havingValue="true")
public class EmailKafkaConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "${notification.email.topic}", groupId = "email-consumer-group")
    public void handleEmailSending(EmailRequestDTO message) {
        try {
            emailService.send(message);
            log.info("Processed Kafka message and sent email to {}", message.getTo());
        } catch (Exception ex) {
            log.error("Failed to send email for message {}", message, ex);
            // production: push to DLQ or store into DB for later retry
        }
    }
}
