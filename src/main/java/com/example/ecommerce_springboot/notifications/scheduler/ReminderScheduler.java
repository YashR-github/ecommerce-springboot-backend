package com.example.ecommerce_springboot.notifications.scheduler;

import com.example.ecommerce_springboot.auth.util.AuthenticatedUserUtil;
import com.example.ecommerce_springboot.ecommerce.models.User;
import com.example.ecommerce_springboot.ecommerce.repository.UserRepository;
import com.example.ecommerce_springboot.notifications.dispatch.DirectNotificationDispatcher;
import com.example.ecommerce_springboot.notifications.dispatch.NotificationDispatcher;
import com.example.ecommerce_springboot.notifications.dispatch.UnifiedNotificationDispatcher;
import com.example.ecommerce_springboot.notifications.dtos.EmailRequestDTO;
import com.example.ecommerce_springboot.notifications.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
//Only works when kafka is true
public class ReminderScheduler {

    private final AuthenticatedUserUtil authenticatedUserUtil;
    private final Optional<NotificationDispatcher> notificationDispatcher; // exists only when use.kafka=true
    private final Optional<DirectNotificationDispatcher> directDispatcher; // exists only when use.kafka=false
    private final EmailService emailService; // used if direct
    private final UnifiedNotificationDispatcher unifiedDispatcher;
    private final UserRepository userRepository;

    // Example: every day at 08:00
    @Scheduled(cron = "${scheduler.cron.daily:0 0 20 * * ?}")
    public void handleScheduledEmails() {
        //Logic to Handle scheduled use cases
//        List<User> allUsers = userRepository.findAll();
//        for (User user : allUsers) {
//            EmailRequestDTO req = new EmailRequestDTO();
//            req.setTo(user.getEmail());
//            req.setSubject("Reminders from Ecommerce Application");
//            req.setContent(buildDailyContent(user, ));
//            unifiedDispatcher.dispatch(req);
//        }
//
//        }

    }


    private String buildScheduledContent(User user) {

        return null;
    }

}








//        // dispatch depending on mode
//        try {
//            // If Kafka enabled, send to Kafka so consumer will handle it
//            if (Boolean.parseBoolean(System.getProperty("use.kafka", "true"))) {
//                // NOTE: NotificationDispatcher may be null if bean not present; check with conditional property
//                notificationDispatcher.dispatchToKafka(req);
//            } else {
//                directDispatcher.dispatchDirect(req);
//            }
//        } catch (Exception ex) {
//            log.error("Failed to dispatch reminder", ex);
//        }