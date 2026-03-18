package com.example.ecommerce_springboot.auth.service;


import com.example.ecommerce_springboot.auth.dtos.AuthLoginResponseDTO;
import com.example.ecommerce_springboot.auth.dtos.UserLoginResponseDTO;
import com.example.ecommerce_springboot.auth.exceptions.InvalidCredentialsException;
import com.example.ecommerce_springboot.auth.exceptions.UserNotFoundException;
import com.example.ecommerce_springboot.auth.security.JwtUtil;
import com.example.ecommerce_springboot.auth.util.UserEntityDtoMapper;
import com.example.ecommerce_springboot.ecommerce.models.User;
import com.example.ecommerce_springboot.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthLoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // interface for bCrypt
    private final JwtUtil jwtUtil;

    public AuthLoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    @Transactional
    public AuthLoginResponseDTO loginUser(String phone, String email, String password) throws UserNotFoundException {
        Optional<User> optionalUser= Optional.empty();

        if(phone!=null && !phone.isBlank() && (email==null || email.isBlank())) {
            optionalUser= userRepository.findByPhoneAndIsDeletedFalse(phone);
            if (optionalUser.isEmpty()) {
                throw new UserNotFoundException("User with phone number not found");
            }
        }
        else if(email!=null && !email.isBlank() && (phone==null || phone.isBlank())) {
            optionalUser= userRepository.findByEmailAndIsDeletedFalse(email);
            if (optionalUser.isEmpty()) {
                throw new UserNotFoundException("User with email not found");
            }
        }
        else { // extra check, this case already handled in controller
            throw new InvalidCredentialsException("Either Phone or Email must be provided");
        }

        User user = optionalUser.get();

        // password match check
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username/email or password");
        }


        // jwt token generation
        String jwtToken = jwtUtil.generateToken(user.getId(), user.getUserRole().name());
        UserLoginResponseDTO userLoginResponseDto = UserEntityDtoMapper.toUserLoginResponseDto(user);

        return new AuthLoginResponseDTO(jwtToken, userLoginResponseDto);
    }
}
