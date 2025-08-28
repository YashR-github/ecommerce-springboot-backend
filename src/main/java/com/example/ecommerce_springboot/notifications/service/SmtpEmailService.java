package com.example.ecommerce_springboot.notifications.service;

import com.example.ecommerce_springboot.notifications.dtos.EmailRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service

@Slf4j
@ConditionalOnProperty(name="email.provider", havingValue="smtp")
public class SmtpEmailService implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;




    @Override
    public void send(EmailRequestDTO request) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(request.getTo());
        msg.setSubject(request.getSubject());
        msg.setText(request.getContent() != null ? request.getContent() : "No content");
        javaMailSender.send(msg);
        log.info("Gmail SMTP email sent to {}", request.getTo());
    }
}
