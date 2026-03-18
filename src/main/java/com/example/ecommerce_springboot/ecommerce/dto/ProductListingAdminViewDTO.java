package com.example.ecommerce_springboot.ecommerce.dto;

import com.example.ecommerce_springboot.ecommerce.enums.ListingStatus;
import com.example.ecommerce_springboot.ecommerce.models.InventoryItem;
import com.example.ecommerce_springboot.ecommerce.models.Product;
import com.example.ecommerce_springboot.ecommerce.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class ProductListingAdminViewDTO {
    private Long listingReviewId;
    private Long listingId;
    private Long sellerId;
    private String sellerName;
    private String sellerEmail;
    private LocalDateTime sellerJoined;
    private Long productId;
    private Integer quantity;
    private BigDecimal basePrice;
    private String title;
    private String description;
    private List<String> imageUrls;
    private LocalDateTime requestedAt;
    private String actionType;
    private String listingStatus;
}
