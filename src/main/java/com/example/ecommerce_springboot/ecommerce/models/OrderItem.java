package com.example.ecommerce_springboot.ecommerce.models;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class OrderItem extends BaseModel {
    @ManyToOne(optional = false, fetch= FetchType.LAZY)
    private CustomerOrder customerOrder;

    @OneToOne(optional= false)
    private CartItem cartItem;

    private Integer quantity;
    @ManyToOne(optional = false, fetch= FetchType.LAZY)
    private ProductListing productListing;

    private BigDecimal priceAtPurchase; //changes based on coupons and promotions applied

}