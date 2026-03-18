package com.example.ecommerce_springboot.ecommerce.exceptions;

public class ListingReviewAuditCreationFailureException extends RuntimeException {
    public ListingReviewAuditCreationFailureException(String message) {
        super(message);
    }
}
