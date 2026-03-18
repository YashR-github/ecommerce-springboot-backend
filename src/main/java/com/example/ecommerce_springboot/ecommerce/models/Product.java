
package com.example.ecommerce_springboot.ecommerce.models;

import com.example.ecommerce_springboot.ecommerce.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product extends BaseModel {
    private String title;
    private String shortDescription;
    private String longDescription;
    private String brand;
    private BigDecimal weightInGrams;
    private String baseImageUrl; //optional feature
    private BigDecimal avgPrice;
    @ManyToOne
    private User productCreator;
    @ManyToOne
    private Category category;
    private String model;
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

}