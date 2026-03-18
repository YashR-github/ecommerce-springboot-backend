package com.example.ecommerce_springboot.ecommerce.enums;

public enum ListingStatus {
    ACTIVE,
    DRAFT,
    ARCHIVED,
    OUT_OF_STOCK,
    PENDING_APPROVAL,  //for seller and admin
    PENDING_DELETION,
    REJECTED, //for seller
    SUSPENDED, // in cases of suspension later
    DELETED

}
