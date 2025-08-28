package com.example.ecommerce_springboot.ecommerce.services;


import com.example.ecommerce_springboot.ecommerce.models.User;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;

public interface PaymentService {

    String generatePaymentLink(Long orderId, Long amount) throws StripeException, RazorpayException;
}
