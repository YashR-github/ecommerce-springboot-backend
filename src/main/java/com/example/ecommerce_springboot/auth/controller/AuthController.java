package com.example.ecommerce_springboot.auth.controller;


import com.example.ecommerce_springboot.auth.dtos.*;
import com.example.ecommerce_springboot.auth.exceptions.InvalidCredentialsException;
import com.example.ecommerce_springboot.auth.service.AuthService;
import com.example.ecommerce_springboot.ecommerce.enums.UserRole;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/seller/signup")
    public ResponseEntity<ResponseDTO<UserSignupResponseDTO>> signupSeller(@RequestBody @Valid UserSignupRequestDTO request) {
        UserSignupResponseDTO userResponseDto = authService.signUp(request.getName(), request.getUsername(), request.getEmail(), request.getPassword(), UserRole.SELLER);
        ResponseDTO<UserSignupResponseDTO> responseDto = new ResponseDTO<>("User Signed Up Successfully!", userResponseDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/customer/signup")
    public ResponseEntity<ResponseDTO<UserSignupResponseDTO>> signupCustomer(@RequestBody @Valid UserSignupRequestDTO request) {
        UserSignupResponseDTO userResponseDto = authService.signUp(request.getName(), request.getUsername(), request.getEmail(), request.getPassword(), UserRole.CUSTOMER);
        ResponseDTO<UserSignupResponseDTO> responseDto = new ResponseDTO<>("User Signed Up Successfully!", userResponseDto);
        return ResponseEntity.ok(responseDto);
    }


    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<UserLoginResponseDTO>> login(@RequestBody @Valid UserLoginRequestDTO request) {
        String username = request.getUsername();
        String email = request.getEmail();

        if ((username == null || username.isBlank()) && (email == null || email.isBlank())) {
            throw new InvalidCredentialsException("Username or Email must be provided");
        }

        AuthLoginResponseDTO authLoginResponseDto = authService.login(username, email, request.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + authLoginResponseDto.getToken());


        ResponseDTO<UserLoginResponseDTO> responseDto = new ResponseDTO<>("User Logged In Successfully!", authLoginResponseDto.getUserLoginResponseDto());
        return new ResponseEntity<>(responseDto, headers, HttpStatus.OK);
    }


}