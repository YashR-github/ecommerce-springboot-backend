package com.example.ecommerce_springboot.ecommerce.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerSearchResultProductListingDTO {
    private Long listingId;
    private String title;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private String category;
    private String brand;
    private String model;

}
