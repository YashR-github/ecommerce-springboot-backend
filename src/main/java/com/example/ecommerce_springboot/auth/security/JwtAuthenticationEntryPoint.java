package com.example.ecommerce_springboot.auth.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override   // Exception handling for unauthorized access to task endpoints
    public void commence(HttpServletRequest request, HttpServletResponse response , AuthenticationException e) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);


        Map<String,Object> errorDetails =new HashMap<>();
        errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorDetails.put("timestamp", System.currentTimeMillis());
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", "Access Denied. Invalid or missing token.");
        errorDetails.put("path", request.getRequestURI());

        objectMapper.writeValue(response.getOutputStream(), errorDetails);
        response.getOutputStream().flush(); // better practice

    }

}
