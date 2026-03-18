package com.example.ecommerce_springboot.ecommerce.models;

import com.example.ecommerce_springboot.ecommerce.enums.CartItemStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(
        name = "cart_item",
        uniqueConstraints =
        @UniqueConstraint(
                columnNames = {"cart_id","product_listing_id"}
        )
)
public class CartItem extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ProductListing productListing;

    private Integer quantity;

    @OneToMany(mappedBy = "reservedCartItem", fetch = FetchType.LAZY)
    private List<InventoryItem> inventoryItems= new ArrayList<>();

    private BigDecimal cartItemPrice;

    @Enumerated(EnumType.STRING)
    private CartItemStatus cartItemStatus;

//    @Enumerated(EnumType.STRING)  future features can be added later
//    private CouponCode couponApplied;
//     private BigDecimal couponDiscount;
//    private List<Coupon> couponsApplied;

}
