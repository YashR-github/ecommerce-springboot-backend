package com.example.ecommerce_springboot.ecommerce.controlleradvice;

import com.example.ecommerce_springboot.auth.dtos.ResponseDTO;
import com.example.ecommerce_springboot.ecommerce.dto.ErrorDto;
import com.example.ecommerce_springboot.ecommerce.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({ProductNotFoundException.class, CategoryNotFoundException.class, ProductListingNotFoundException.class, ReviewRequestNotFoundException.class, CartItemNotFoundException.class})
    public ResponseEntity<ResponseDTO<Map<String, Object>>> handleNotFoundExceptions(
            ResourceNotFoundException ex, WebRequest request) {

        Map<String, Object> errorDetails = new HashMap<>();

        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("error", "Entity Not Found");
        errorDetails.put("details", ex.getMessage());
        errorDetails.put("status", HttpStatus.NOT_FOUND.value());
        errorDetails.put(
                "path",
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );

        ResponseDTO<Map<String, Object>> responseDTO =
                new ResponseDTO<>("Request Failed", errorDetails);

        return new ResponseEntity<>(
                responseDTO,
                HttpStatus.NOT_FOUND
        );
    }


    @ExceptionHandler({SellerProductAlreadyExistException.class, CategoryAlreadyExistException.class})
    public ResponseEntity<ResponseDTO<Map<String, Object>>> handleAlreadyExistsExceptions(
            ResourceAlreadyExistException ex, WebRequest request) {

        Map<String, Object> errorDetails = new HashMap<>();

        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("error", "Entity Already Exists");
        errorDetails.put("details", ex.getMessage());
        errorDetails.put("status", HttpStatus.CONFLICT.value());
        errorDetails.put(
                "path",
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );

        ResponseDTO<Map<String, Object>> responseDTO =
                new ResponseDTO<>("Request Failed", errorDetails);

        return new ResponseEntity<>(
                responseDTO,
                HttpStatus.CONFLICT
        );
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<Map<String, Object>>> handleException(
            Exception ex, WebRequest request) {

        Map<String, Object> errorDetails = new HashMap<>();

        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("error", "Internal Server Error");
        errorDetails.put("details", ex.getMessage());
        errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.put(
                "path",
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );

        ResponseDTO<Map<String, Object>> responseDTO =
                new ResponseDTO<>("Request Failed", errorDetails);

        return new ResponseEntity<>(
                responseDTO,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
