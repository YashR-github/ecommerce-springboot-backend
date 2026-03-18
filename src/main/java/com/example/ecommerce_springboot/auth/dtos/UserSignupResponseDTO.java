package com.example.ecommerce_springboot.auth.dtos;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserSignupResponseDTO {

    private String name;

    private String phone;

    private String email;

    private String userRole;
}
