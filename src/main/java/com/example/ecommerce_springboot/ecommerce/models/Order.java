package com.example.ecommerce_springboot.ecommerce.models;


import com.example.ecommerce_springboot.ecommerce.enums.OrderStatus;
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
public class Order extends BaseModel{

    @ManyToOne
    private User user;
    @OneToMany(mappedBy ="order", cascade =CascadeType.ALL,orphanRemoval =true)
    private List<OrderItem> orderItems;
    private BigDecimal orderTotal;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;
    private LocalDateTime reservationExpiryTime;
    @OneToOne
    private Payment payment;


    public void addItems(List<OrderItem> items){
        items.forEach(i->i.setOrder(this));
        this.orderItems.addAll(items);
    }

}
