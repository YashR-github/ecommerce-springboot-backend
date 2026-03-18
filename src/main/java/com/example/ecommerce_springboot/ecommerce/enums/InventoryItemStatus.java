package com.example.ecommerce_springboot.ecommerce.enums;


public enum InventoryItemStatus {
//    PENDING_LISTING_APPROVAL,
    AVAILABLE,
    RESERVED_IN_CART,
    RESERVED_FOR_ORDER,
    ORDER_COMPLETED,
    IN_DELIVERY,
    DELIVERED,
    RETURN_REQUESTED,
    RETURNED,
    OUT_OF_STOCK,
    DAMAGED_UNSELLABLE
}
