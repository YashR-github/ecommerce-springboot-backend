package com.example.ecommerce_springboot.ecommerce.dto;

import com.example.ecommerce_springboot.ecommerce.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class ProductUpdateReqDTO {

    @NotNull(message = "Product Id is required")
    private Long productId;
    private String title;
    private String shortDescription;
    private String longDescription;
    private String brand;
    private BigDecimal weightInGrams;
    private String baseImageUrl; //optional feature
    private Long categoryId;
    private ProductStatus productStatus;
}
