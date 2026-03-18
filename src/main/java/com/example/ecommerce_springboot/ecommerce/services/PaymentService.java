package com.example.ecommerce_springboot.ecommerce.services;



import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;

import java.math.BigDecimal;

public interface PaymentService {

    String generatePaymentLink(Long orderId, BigDecimal amount) throws StripeException, RazorpayException;
}
