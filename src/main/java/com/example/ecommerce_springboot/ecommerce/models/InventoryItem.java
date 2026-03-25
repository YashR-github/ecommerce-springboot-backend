package com.example.ecommerce_springboot.ecommerce.models;


import com.example.ecommerce_springboot.ecommerce.enums.InventoryItemStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_item")
@Getter
@Setter
public class InventoryItem extends BaseModel{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ProductListing productListing;

    @Enumerated(EnumType.STRING)
    @Column(name= "item_status")
    private InventoryItemStatus itemStatus;

    private BigDecimal itemPrice;
    private String uniqueScanCode; //future scalability, to be filled by warehouse scanning

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserved_cart_item_id")
    private CartItem reservedCartItem;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserved_order_id")
    private CustomerOrder reservedOrder;


    private LocalDateTime reservedAt;

    @Column(name = "reservation_expiry_time")
    private LocalDateTime reservationExpiryTime;
}
