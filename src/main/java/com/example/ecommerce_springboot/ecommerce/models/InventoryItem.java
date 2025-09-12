package com.example.ecommerce_springboot.ecommerce.models;


import com.example.ecommerce_springboot.ecommerce.enums.InventoryItemStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class InventoryItem extends BaseModel{

    @ManyToOne
    private ProductListing productListing;

    private InventoryItemStatus itemStatus;


}
