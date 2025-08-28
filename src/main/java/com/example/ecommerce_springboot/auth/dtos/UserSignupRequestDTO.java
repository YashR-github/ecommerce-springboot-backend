package com.example.ecommerce_springboot.auth.dtos;


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

    @NotBlank(message="Username is required")
    private String username;

    @Email(message= "Invalid email")
    private String email;


    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;


}
