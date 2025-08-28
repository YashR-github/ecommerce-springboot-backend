package com.example.ecommerce_springboot.notifications.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO implements Serializable {
    private String to;
    private String subject;
    private String template;          // optional template name
    private Map<String, Object> model; // optional model for template
    private String content;           // optional simple content text/html
//TODO Future updates could add more attributes for other features in email like attachments, images, cc, bcc etc
}

