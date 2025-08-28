package com.example.ecommerce_springboot.ecommerce.models;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class OrderItem extends BaseModel{

    @ManyToOne
    private User user;
    @ManyToOne
    private Product product;
    private int quantity;

    private double totalPrice;
    @ManyToOne
    private Order order;
}
