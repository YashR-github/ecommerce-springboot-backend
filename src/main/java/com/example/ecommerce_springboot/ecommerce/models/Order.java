package com.example.ecommerce_springboot.ecommerce.models;


import com.example.ecommerce_springboot.ecommerce.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Order extends BaseModel{

    @ManyToOne
    private User user;
    @OneToMany
    private List<OrderItem> orderItems;
    private double orderTotal;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY)
    private List<Payment> payments;

}
