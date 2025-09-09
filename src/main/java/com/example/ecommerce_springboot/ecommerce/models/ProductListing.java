package com.example.ecommerce_springboot.ecommerce.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
//acts as a container or a seller inventory declaration of a particular product
public class ProductListing extends BaseModel{

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;
    private int quantity;
    private double perPrice;

    private String sellerTitle;
    private String sellerDescription;

}
