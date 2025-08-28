package com.example.ecommerce_springboot.auth.service;


import com.example.ecommerce_springboot.auth.dtos.AuthLoginResponseDTO;
import com.example.ecommerce_springboot.auth.dtos.UserSignupResponseDTO;
import com.example.ecommerce_springboot.auth.exceptions.UserAlreadyExistException;
import com.example.ecommerce_springboot.auth.exceptions.UserNotFoundException;
import com.example.ecommerce_springboot.ecommerce.enums.UserRole;

public interface AuthService {

    public UserSignupResponseDTO signUp(String name, String username, String email, String password, UserRole userRole) throws UserAlreadyExistException;

    public AuthLoginResponseDTO login(String username, String email , String password) throws UserNotFoundException;

}
