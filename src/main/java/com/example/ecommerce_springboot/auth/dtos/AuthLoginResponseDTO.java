package com.example.ecommerce_springboot.auth.dtos;


import lombok.Data;

@Data
public class AuthLoginResponseDTO {
    private String token;
    private UserLoginResponseDTO userLoginResponseDto;

    public AuthLoginResponseDTO(String token, UserLoginResponseDTO userLoginResponseDto) {
        this.token = token;
        this.userLoginResponseDto = userLoginResponseDto;
    }
}
