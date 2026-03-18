package com.example.ecommerce_springboot.ecommerce.dto;


import lombok.Data;

@Data
public class AdminListingReviewAuditDecisionDTO {
    private Long auditId;
    private String reason;
}
