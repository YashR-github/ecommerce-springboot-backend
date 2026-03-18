package com.example.ecommerce_springboot.auth.service;


import com.example.ecommerce_springboot.auth.dtos.AuthLoginResponseDTO;
import com.example.ecommerce_springboot.auth.dtos.UserSignupResponseDTO;
import com.example.ecommerce_springboot.auth.exceptions.UserAlreadyExistException;
import com.example.ecommerce_springboot.auth.exceptions.UserNotFoundException;
import com.example.ecommerce_springboot.ecommerce.enums.UserRole;

public interface AuthService {

    UserSignupResponseDTO signUp(String name, String phone, String email, String password, UserRole userRole) throws UserAlreadyExistException;

    AuthLoginResponseDTO login(String phone, String email , String password) throws UserNotFoundException;

}
