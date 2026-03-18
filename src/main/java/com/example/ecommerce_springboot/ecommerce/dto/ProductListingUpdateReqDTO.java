package com.example.ecommerce_springboot.ecommerce.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductListingUpdateReqDTO {
    private Long productId;
    private Integer quantity;
    private BigDecimal basePrice;
    private String title;
    private String description;
    private List<String> imageUrls;
}
