package com.example.ecommerce_springboot.ecommerce.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderItemDTO {
    private Long listingId;
    private String title;
    private Integer quantity;
    private String imageUrl;
    private LocalDateTime updatedAt;
    private BigDecimal totalPrice;
}
