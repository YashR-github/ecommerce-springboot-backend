package com.example.ecommerce_springboot.ecommerce.models;


import com.example.ecommerce_springboot.ecommerce.enums.OrderStatus;
import com.example.ecommerce_springboot.ecommerce.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name ="orders")
public class CustomerOrder extends BaseModel{

    @ManyToOne
    private User user;
    @OneToMany(mappedBy ="order", cascade =CascadeType.ALL,orphanRemoval =true)
    private List<OrderItem> orderItems;
    private BigDecimal orderTotal;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private LocalDateTime orderDate;
    private LocalDateTime reservationExpiryTime;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    private String cancellationReason;


//    public void addItems(List<OrderItem> items){
//        items.forEach(i->i.setCustomerOrder(this));
//        this.orderItems.addAll(items);
//    }

}
