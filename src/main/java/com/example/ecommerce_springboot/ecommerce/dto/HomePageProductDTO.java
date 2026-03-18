package com.example.ecommerce_springboot.ecommerce.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class HomePageProductDTO {
    private Long productId;
    private String title;
    private String category;
    private String brand;
    private String model;
    private String imageUrl;
    private BigDecimal price;
//    private Integer quantity;
    private String shortDescription;

}
