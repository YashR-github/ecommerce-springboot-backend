package com.example.ecommerce_springboot.ecommerce.dto;


import com.example.ecommerce_springboot.ecommerce.enums.ListingStatus;
import com.example.ecommerce_springboot.ecommerce.models.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductListingCustomerResponseDTO {
    private Long listingId;
    private String sellerName;
    private Long sellerID;
    private String sellerEmail;
    private String category;
    private String brand;
    private String model;
    private BigDecimal weightInGrams;
    private Integer quantityRemaining;
    private BigDecimal price;
    private String title; //subject of listing
    private String description;//comments on listing
    private List<String> imageUrls;
    private String listingStatus;
}

