package com.example.ecommerce_springboot.notifications.dispatch;

import com.example.ecommerce_springboot.notifications.dtos.EmailRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnifiedNotificationDispatcher {

    private final Optional<NotificationDispatcher> notificationDispatcher; // Kafka
    private final Optional<DirectNotificationDispatcher> directDispatcher; // Direct

    @Value("${use.kafka:true}")
    private boolean useKafka; // injected property instead of System.getProperty

    public void dispatch(EmailRequestDTO request) {
        try {
            if (useKafka) {
                if (notificationDispatcher.isPresent()) {
                    notificationDispatcher.get().dispatchToKafka(request);
                } else {
                    log.warn("Kafka dispatcher bean not available, falling back to direct dispatch");
                    directDispatcher.get().dispatchDirect(request);
                }
            } else {
                directDispatcher.get().dispatchDirect(request);
            }
        } catch (Exception ex) {
            log.error("Failed to dispatch email", ex);
            // optionally: push to retry mechanism or DLQ
        }
    }
}

