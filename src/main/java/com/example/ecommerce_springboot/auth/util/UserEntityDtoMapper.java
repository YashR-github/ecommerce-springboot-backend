package com.example.ecommerce_springboot.auth.util;


import com.example.ecommerce_springboot.auth.dtos.UserLoginResponseDTO;
import com.example.ecommerce_springboot.auth.dtos.UserSignupResponseDTO;
import com.example.ecommerce_springboot.ecommerce.models.User;
import org.springframework.stereotype.Component;


@Component
public class UserEntityDtoMapper {


    public static UserSignupResponseDTO toUserSignupResponseDto(User user){
        UserSignupResponseDTO userSignupResponseDTO = new UserSignupResponseDTO();
        userSignupResponseDTO.setName(user.getName());
//        userSignupResponseDTO.setUsername(user.getUsername());
        userSignupResponseDTO.setEmail(user.getEmail());
        userSignupResponseDTO.setUserRole(user.getUserRole().name());
        return userSignupResponseDTO;
    }

    public static UserLoginResponseDTO toUserLoginResponseDto(User user){
    UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
    if(user.getEmail() != null){
    userLoginResponseDTO.setEmail(user.getEmail()); }
    userLoginResponseDTO.setUserRole(user.getUserRole().name());
    return userLoginResponseDTO;
    }
}
