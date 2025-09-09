
package com.example.ecommerce_springboot.ecommerce.models;

import com.example.ecommerce_springboot.ecommerce.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product extends BaseModel {
    private String title;
    private String description;
//    private Double price;
    private String imageUrl;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;




//    @Enumerated(EnumType.STRING)
//    private ProductStatus productStatus;

//    @ManyToOne
//    @JoinColumn(name = "seller_id")
//    private User seller;

}