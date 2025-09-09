package com.example.ecommerce_springboot.ecommerce.models;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class CartItem extends BaseModel {

    @ManyToOne
    private Product product;
    private int quantity;
    private double totalCartItemsPrice;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
}
