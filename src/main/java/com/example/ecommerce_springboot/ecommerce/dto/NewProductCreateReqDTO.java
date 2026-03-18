package com.example.ecommerce_springboot.ecommerce.dto;

import com.example.ecommerce_springboot.ecommerce.enums.CategoryType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NewProductCreateReqDTO {
    @NotNull(message = "Product name is required.")
    private String name;
    @NotNull(message = "Product description is required")
    @Min(value = 50, message = "Product short description must be at least 50 characters long.")
    private String shortDescription;
    @Max(value = 1000, message = "Product long description must be less than 1000 characters long.")
    private String longDescription;
    @NotNull(message = "Product brand or manufacturer name required.")
    private String brand;
    private BigDecimal weightInGrams;
    @NotNull(message = "Product base price is required.")
    private BigDecimal basePrice;
    private String baseImageUrl; //optional feature
    @NotNull(message = "Category is required.")
    private CategoryType category; // seller passes the existing category

    @NotBlank
    @NotNull(message= "Product model required")
    private String model;

}
