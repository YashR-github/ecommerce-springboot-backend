package com.example.ecommerce_springboot.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class ProductCreateResponseDto {
    private Long productId;
    private LocalDateTime createdAt;
    private String title;
    private String shortDescription;
    private String longDescription;
    private String brand;
    private String model;
    private BigDecimal weightInGrams;
    private String baseImageUrl; //optional feature
    private Long categoryId;
}
