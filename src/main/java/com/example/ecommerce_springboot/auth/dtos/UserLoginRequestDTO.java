package com.example.ecommerce_springboot.auth.dtos;


import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestDTO {

    @Email(message="Please enter a valid email")
    private String email;
    @Digits(integer = 10, fraction = 0, message = "Phone number must be 10 digits")
    private String phone;
    @NotBlank(message="Password is required")
    private String password; //required

}
