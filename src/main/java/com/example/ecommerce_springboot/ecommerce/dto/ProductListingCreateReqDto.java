package com.example.ecommerce_springboot.ecommerce.dto;

import com.example.ecommerce_springboot.ecommerce.models.Product;
import com.example.ecommerce_springboot.ecommerce.models.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class ProductListingCreateReqDto {

    @NotNull(message = "Product Id is required")
    private Long productId;
    @NotNull(message = "Quantity is required")
    private Integer quantity;
    private BigDecimal basePrice;
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    private List<String> imageUrls; //optional

}
