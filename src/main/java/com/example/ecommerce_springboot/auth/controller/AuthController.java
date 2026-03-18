package com.example.ecommerce_springboot.auth.controller;


import com.example.ecommerce_springboot.auth.dtos.*;
import com.example.ecommerce_springboot.auth.exceptions.InvalidCredentialsException;
import com.example.ecommerce_springboot.auth.service.AuthLoginService;
import com.example.ecommerce_springboot.auth.service.AuthService;
import com.example.ecommerce_springboot.auth.util.AuthenticatedUserUtil;
import com.example.ecommerce_springboot.ecommerce.enums.UserRole;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final Map<String,AuthService> authServiceMap;
    private final AuthenticatedUserUtil authenticatedUserUtil;


    public AuthController(Map<String, AuthService> authServiceMap, AuthenticatedUserUtil authenticatedUserUtil) {
        this.authServiceMap = authServiceMap;
        this.authenticatedUserUtil = authenticatedUserUtil;
    }

    @PostMapping("/seller/signup")
    public ResponseEntity<ResponseDTO<UserSignupResponseDTO>> signupSeller(@RequestBody @Valid UserSignupRequestDTO request) {
        AuthService authService = authServiceMap.get(UserRole.SELLER.name());
        UserSignupResponseDTO userResponseDto = authService.signUp(request.getName(), request.getPhone(), request.getEmail(), request.getPassword(), UserRole.SELLER);
        ResponseDTO<UserSignupResponseDTO> responseDto = new ResponseDTO<>("User Signed Up Successfully!", userResponseDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/customer/signup")
    public ResponseEntity<ResponseDTO<UserSignupResponseDTO>> signupCustomer(@RequestBody @Valid UserSignupRequestDTO request) {
        AuthService authService = authServiceMap.get(UserRole.CUSTOMER.name());
        UserSignupResponseDTO userResponseDto = authService.signUp(request.getName(), request.getPhone(), request.getEmail(), request.getPassword(), UserRole.CUSTOMER);
        ResponseDTO<UserSignupResponseDTO> responseDto = new ResponseDTO<>("User Signed Up Successfully!", userResponseDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/admin/signup")
    public ResponseEntity<ResponseDTO<UserSignupResponseDTO>> signupAdmin(@RequestBody @Valid UserSignupRequestDTO request) {
        AuthService authService = authServiceMap.get(UserRole.ADMIN.name());
        UserSignupResponseDTO userResponseDto = authService.signUp(request.getName(), request.getPhone(), request.getEmail(), request.getPassword(), UserRole.ADMIN);
        ResponseDTO<UserSignupResponseDTO> responseDto = new ResponseDTO<>("User Signed Up Successfully!", userResponseDto);
        return ResponseEntity.ok(responseDto);
    }


    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<UserLoginResponseDTO>> login(@RequestBody @Valid UserLoginRequestDTO request) {
        String phone = request.getPhone();
        String email = request.getEmail();

        if ((phone == null || phone.isBlank()) && (email == null || email.isBlank())) {
            throw new InvalidCredentialsException("Phone or Email must be provided");
        }

        AuthService authService = authServiceMap.get(getCurrentUserRole());
        AuthLoginResponseDTO authLoginResponseDto = authService.login(phone, email, request.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + authLoginResponseDto.getToken());


        ResponseDTO<UserLoginResponseDTO> responseDto = new ResponseDTO<>("User Logged In Successfully!", authLoginResponseDto.getUserLoginResponseDto());
        return new ResponseEntity<>(responseDto, headers, HttpStatus.OK);
    }



    //------------------------------ helper method -------------------------------------------------------------------------------------------------------------

    private String getCurrentUserRole() {
        return authenticatedUserUtil.getCurrentUser().getUserRole().name();
    }


}