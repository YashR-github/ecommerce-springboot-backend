package com.example.ecommerce_springboot.ecommerce.controller;


import com.example.ecommerce_springboot.ecommerce.dto.CustomerOrderResponseDTO;
import com.example.ecommerce_springboot.ecommerce.services.CustomerService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final CustomerService customerService;

    public OrderController(CustomerService customerService) {
        this.customerService = customerService;
    }


//---------------------------- Customer specific APIs ----------------------------------------------------------------------------



    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/customers/checkout")
    public ResponseEntity<CustomerOrderResponseDTO> moveCartToOrderFlow() throws ExecutionException, InterruptedException {
    CustomerOrderResponseDTO response = customerService.createOrderForCart();
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



}
