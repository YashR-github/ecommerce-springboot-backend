package com.example.ecommerce_springboot.ecommerce.controller;


import com.example.ecommerce_springboot.ecommerce.dto.GeneratePaymentLinkRequestDto;
import com.example.ecommerce_springboot.ecommerce.services.PaymentService;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private PaymentService paymentService;
    public PaymentController(@Qualifier("stripePaymentGatewayImpl") PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping(value="/customers/payments")
    public ResponseEntity<String> createPaymentLink(@RequestBody GeneratePaymentLinkRequestDto paymentLinkRequestDto) throws StripeException, RazorpayException {
          String paymentLink = paymentService.generatePaymentLink(paymentLinkRequestDto.getOrderId(), paymentLinkRequestDto.getAmount());

          return new ResponseEntity<>(paymentLink, HttpStatus.OK);
    }



    @PostMapping("/webhook")
    public void handleWebhook(){
        System.out.println("Webhook received here");
    }
}
