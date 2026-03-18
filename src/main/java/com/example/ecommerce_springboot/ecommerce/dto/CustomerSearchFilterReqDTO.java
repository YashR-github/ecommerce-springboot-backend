package com.example.ecommerce_springboot.ecommerce.dto;


import lombok.Data;

@Data
public class CustomerSearchFilterReqDTO {

    private String keyword;
    private String sortBy;
}
