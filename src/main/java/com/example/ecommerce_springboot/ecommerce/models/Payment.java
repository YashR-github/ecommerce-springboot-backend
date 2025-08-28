package com.example.ecommerce_springboot.ecommerce.models;


import com.example.ecommerce_springboot.ecommerce.enums.PaymentStatus;
import com.example.ecommerce_springboot.ecommerce.enums.PaymentType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Payment extends BaseModel{
    private String paymentReference;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private String paymentMessage;

    @ManyToOne
    private Order order;
}
