package com.example.ecommerce_springboot.auth.controlleradvice;


import com.example.ecommerce_springboot.auth.dtos.ResponseDTO;
import com.example.ecommerce_springboot.auth.exceptions.InvalidCredentialsException;
import com.example.ecommerce_springboot.auth.exceptions.UserAlreadyExistException;
import com.example.ecommerce_springboot.auth.exceptions.UserNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice(basePackages= {"org.example.ecommerce_springboot.auth"})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionHandler {


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseDTO<Map<String, Object>>> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("error", "User not Found");
        errorDetails.put("details", ex.getMessage());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        errorDetails.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>("Login Failed", errorDetails);
        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ResponseDTO<Map<String, Object>>> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("error", "Invalid Credentials.");
        errorDetails.put("details", ex.getMessage());
        errorDetails.put("status", HttpStatus.UNAUTHORIZED.value());
        errorDetails.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>("Login Failed", errorDetails);
        return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ResponseDTO<Map<String, Object>>> handleUserAlreadyExistException(UserAlreadyExistException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("error", "User Already Exists");
        errorDetails.put("details", ex.getMessage());
        errorDetails.put("status", HttpStatus.CONFLICT.value());
        errorDetails.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>("Registration Failed", errorDetails);
        return new ResponseEntity<>(responseDTO, HttpStatus.CONFLICT);
    }

}