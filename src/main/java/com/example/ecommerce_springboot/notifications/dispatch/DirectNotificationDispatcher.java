package com.example.ecommerce_springboot.notifications.dispatch;


import com.example.ecommerce_springboot.notifications.dtos.EmailRequestDTO;
import com.example.ecommerce_springboot.notifications.service.EmailService;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name="use.kafka", havingValue="false", matchIfMissing=true)
public class DirectNotificationDispatcher {
    private final EmailService emailService;

    public void dispatchDirect(EmailRequestDTO request) throws Exception {
        emailService.send(request);
    }
}

