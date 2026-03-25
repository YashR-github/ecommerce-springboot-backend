package com.example.ecommerce_springboot.ecommerce.controller;

import com.example.ecommerce_springboot.ecommerce.services.CustomerService;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final CustomerService customerService;

    public PaymentController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{orderId}/initiate")
    public ResponseEntity<String> initiatePayment( @PathVariable Long orderId) throws StripeException, RazorpayException {
        String paymentLink =
                customerService.initiatePaymentForOrder(orderId);

        return ResponseEntity.ok(paymentLink);
    }



    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String rawPayload, @RequestHeader("Stripe-Signature") String stripeSignature) {
        customerService.handleStripeWebhook(rawPayload, stripeSignature);
        return new ResponseEntity<>("Webhook processed", HttpStatus.OK);
    }


}
