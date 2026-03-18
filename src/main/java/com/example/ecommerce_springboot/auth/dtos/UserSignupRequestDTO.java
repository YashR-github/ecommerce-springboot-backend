package com.example.ecommerce_springboot.auth.dtos;


import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserSignupRequestDTO {

    @NotBlank(message="Name is required")
    private String name;

    @NotBlank(message="Phone number is required")
    @Digits(integer = 10, fraction = 0, message = "Phone number must be 10 digits")
    private String phone;

    @Email(message= "Please enter a valid email address")
    @NotBlank(message="Email is required")
    private String email;


    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;


}
