package com.example.ecommerce_springboot.ecommerce.models;


import com.example.ecommerce_springboot.ecommerce.enums.PaymentGateway;
import com.example.ecommerce_springboot.ecommerce.enums.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Payment extends BaseModel{
    @ManyToOne(optional=false)
    private CustomerOrder order;

    private String gatewayReferenceId;
    private String gatewayTransactionId;
    private String paymentLink;
    @Enumerated(EnumType.STRING)
    private PaymentGateway gateway;
    private Integer attemptNo;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private String failureReason;
    private LocalDateTime completedAt;

}
