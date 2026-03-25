package com.example.ecommerce_springboot.ecommerce.models;

import com.example.ecommerce_springboot.ecommerce.enums.ListingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
//acts as a container or a 'seller inventory declaration' of a particular single product
public class ProductListing extends BaseModel{

    @ManyToOne
    private Product product;
    @ManyToOne
    private User listingCreator;
    private Integer quantityListed;
    private BigDecimal basePrice;
    private String title; //subject of listing
    private String description;//comments on listing
    private List<String> imageUrls;
    @Enumerated(EnumType.STRING)
    private ListingStatus listingStatus;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;

    @OneToMany(mappedBy = "productListing", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InventoryItem> inventoryItems;

}
