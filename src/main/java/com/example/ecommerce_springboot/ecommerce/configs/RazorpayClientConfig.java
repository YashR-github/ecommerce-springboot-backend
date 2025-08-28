package com.example.ecommerce_springboot.ecommerce.configs;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayClientConfig {

    @Value("${razorpay.key.id}")
    private  String razorpayKeyId;
    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;


    @Bean
    public RazorpayClient createRazorPayClient() throws RazorpayException {
        return new RazorpayClient(razorpayKeyId,razorpayKeySecret);
    }
}
