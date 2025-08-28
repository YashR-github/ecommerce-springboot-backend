package com.example.ecommerce_springboot.ecommerce.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.util.Map;

@Entity
@Data
public class Inventory extends BaseModel{

    @OneToOne()
    private Product product;
    private Integer quantity;
}
