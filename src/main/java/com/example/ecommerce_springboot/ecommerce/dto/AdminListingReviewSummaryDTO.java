package com.example.ecommerce_springboot.ecommerce.dto;

import com.example.ecommerce_springboot.ecommerce.models.User;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class AdminListingReviewSummaryDTO {
    private Long listingId;
    private String listingTitle;
    private Long productId;
    private Integer quantity;
    private String actionType;
    private User requestedBy;
    private LocalDateTime requestedAt;
    private User reviewedBy;
    private LocalDateTime reviewedAt;
    private String reviewStatus;
    private String reason;

}
