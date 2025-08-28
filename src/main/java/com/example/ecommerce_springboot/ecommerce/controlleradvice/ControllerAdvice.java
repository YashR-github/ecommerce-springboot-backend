package com.example.ecommerce_springboot.ecommerce.controlleradvice;

import com.example.ecommerce_springboot.ecommerce.dto.ErrorDto;
import com.example.ecommerce_springboot.ecommerce.exceptions.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorDto> handleProductNotFoundException(Exception e){
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage(e.getMessage()); // getMessage extracts only the readable message from stack trace message, setMessage sets the message to object errorDto
        ResponseEntity<ErrorDto> response = new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
        return response;
    }

}
