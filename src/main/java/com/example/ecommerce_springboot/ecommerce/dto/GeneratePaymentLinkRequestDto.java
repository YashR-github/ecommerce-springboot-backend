package com.example.ecommerce_springboot.ecommerce.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GeneratePaymentLinkRequestDto {

    private Long orderId;
    private BigDecimal amount;

}
