package com.example.ecommerce_springboot.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartItemDTO {
    private Long listingId;
    private String listingTitle;
    private Integer quantity;
    private String imageUrl;
    private LocalDateTime updatedAt;
    private BigDecimal cartItemPrice;
}
