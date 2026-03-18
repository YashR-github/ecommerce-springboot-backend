package com.example.ecommerce_springboot.ecommerce.models;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderItem extends BaseModel {
    @ManyToOne(optional = false, fetch= FetchType.LAZY)
    private Order order;

    @OneToOne(optional= false)
    private CartItem cartItem;

    private double itemFinalPrice; //changes based on coupons and promotions applied

}