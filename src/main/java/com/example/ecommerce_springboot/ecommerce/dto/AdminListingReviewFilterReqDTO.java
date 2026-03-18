package com.example.ecommerce_springboot.ecommerce.dto;


import com.example.ecommerce_springboot.ecommerce.enums.ReviewStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AdminListingReviewFilterReqDTO {
    private Long listingId;
    private Long productId;
    private Long sellerId;
    private ReviewStatus reviewStatus;
    private LocalDate requestedAt;
    private Integer pageNumber;
    private Integer pageSize=10;
    private String sortBy="requestedAt";
    private String sortOrder="asc";
}
