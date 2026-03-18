package com.example.ecommerce_springboot.ecommerce.dto;


import lombok.Data;

@Data
public class SellerProductListingsFilterReqDTO {
    private Long reviewId;
    private Long listingId;
    private Long productId;
    private String keyword;
    private String reviewStatus;
    private String actionType;
    private Integer pageNumber=0;
    private Integer pageSize=15;
    private String sortBy;
    private String sortOrder;
    private String category;
}
