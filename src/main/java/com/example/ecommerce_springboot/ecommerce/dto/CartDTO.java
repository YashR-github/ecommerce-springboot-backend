package com.example.ecommerce_springboot.ecommerce.dto;

import com.example.ecommerce_springboot.ecommerce.enums.CartStatus;
import com.example.ecommerce_springboot.ecommerce.models.CartItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDTO {
    private Long userId;
    private List<CartItem> cartItems;
    private CartStatus cartStatus;
    private BigDecimal cartTotal;
}
