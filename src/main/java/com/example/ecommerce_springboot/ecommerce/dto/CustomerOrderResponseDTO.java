package com.example.ecommerce_springboot.ecommerce.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CustomerOrderResponseDTO {
    private Long orderId;
    private List<OrderItemDTO> orderItems;
    private BigDecimal totalOrderAmount;
    private String orderStatus;
}
