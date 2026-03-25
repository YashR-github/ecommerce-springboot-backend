package com.example.ecommerce_springboot.ecommerce.models;


import com.example.ecommerce_springboot.ecommerce.enums.ActionType;
import com.example.ecommerce_springboot.ecommerce.enums.ReviewStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ListingReviewAudit extends BaseModel {
    private Long listingId;
    private String title;
    private Long productId;
    @Enumerated(EnumType.STRING)
    private ActionType actionType;
    @ManyToOne
    private User requestedBy;
    private LocalDateTime requestedAt;
    private Integer quantity;
    @ManyToOne
    private User reviewedBy;
    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus= ReviewStatus.PENDING;
    private String reason;
    private LocalDateTime reviewedAt;

}
