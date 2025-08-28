package com.example.ecommerce_springboot.notifications.service;

import com.example.ecommerce_springboot.notifications.dtos.EmailRequestDTO;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;

import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@ConditionalOnProperty(name="email.provider", havingValue="sendgrid")
public class SendGridEmailService implements EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Override
    public void send(EmailRequestDTO request) throws IOException {
        Email from = new Email(fromEmail);
        Email to = new Email(request.getTo());
        String subject = request.getSubject();

        // Build content: prefer request.content else simple render from template+model
        String body = request.getContent();
        if (body == null) body = buildSimpleHtmlFromTemplate(request);

        Content content = new Content("text/html", body);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request sgRequest = new Request();
        sgRequest.setMethod(Method.POST);
        sgRequest.setEndpoint("mail/send");
        sgRequest.setBody(mail.build());

        Response response = sg.api(sgRequest);
        log.info("SendGrid response: status={}, body={}", response.getStatusCode(), response.getBody());
        if (response.getStatusCode() >= 400) {
            throw new RuntimeException("SendGrid failed with status: " + response.getStatusCode());
        }
    }


    // simple fallback builder (can replace with Thymeleaf/FreeMarker)
    private String buildSimpleHtmlFromTemplate(EmailRequestDTO req) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        if (req.getModel() != null && req.getModel().containsKey("username")) {
            sb.append("<p>Hi ").append(req.getModel().get("username")).append(",</p>");
        }
        sb.append("<div>");
        if (req.getContent() != null) sb.append(req.getContent());
        else sb.append("<p>No content</p>");
        sb.append("</div>");
        sb.append("<hr/><p>Task Pilot</p>");
        sb.append("</body></html>");
        return sb.toString();
    }
}

