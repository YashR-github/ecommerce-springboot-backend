package com.example.ecommerce_springboot.auth.service;


import com.example.ecommerce_springboot.auth.dtos.AuthLoginResponseDTO;
import com.example.ecommerce_springboot.auth.dtos.UserLoginResponseDTO;
import com.example.ecommerce_springboot.auth.dtos.UserSignupResponseDTO;
import com.example.ecommerce_springboot.auth.exceptions.InvalidCredentialsException;
import com.example.ecommerce_springboot.auth.exceptions.UserAlreadyExistException;
import com.example.ecommerce_springboot.auth.exceptions.UserNotFoundException;
import com.example.ecommerce_springboot.auth.security.JwtUtil;
import com.example.ecommerce_springboot.auth.util.UserEntityDtoMapper;
import com.example.ecommerce_springboot.ecommerce.enums.UserRole;
import com.example.ecommerce_springboot.ecommerce.models.User;
import com.example.ecommerce_springboot.ecommerce.repository.UserRepository;
import com.example.ecommerce_springboot.notifications.dispatch.UnifiedNotificationDispatcher;
import com.example.ecommerce_springboot.notifications.dtos.EmailRequestDTO;
import jakarta.transaction.Transactional;
//import org.example.codetodoapplicationpersonal.notifications.dispatch.UnifiedNotificationDispatcher;
//import org.example.codetodoapplicationpersonal.notifications.dtos.EmailRequestDTO;
//import org.example.codetodoapplicationpersonal.user.dtos.AuthLoginResponseDTO;
//import org.example.codetodoapplicationpersonal.user.dtos.UserLoginResponseDTO;
//import org.example.codetodoapplicationpersonal.user.dtos.UserSignupResponseDTO;
//import org.example.codetodoapplicationpersonal.user.enums.UserRole;
//import org.example.codetodoapplicationpersonal.user.enums.UserStatus;
//import org.example.codetodoapplicationpersonal.user.exceptions.InvalidCredentialsException;
//import org.example.codetodoapplicationpersonal.user.exceptions.UserAlreadyExistException;
//import org.example.codetodoapplicationpersonal.user.exceptions.UserNotFoundException;
//import org.example.codetodoapplicationpersonal.user.model.User;
//import org.example.codetodoapplicationpersonal.user.repositories.UserRepository;
//import org.example.codetodoapplicationpersonal.user.security.JwtUtil;
//import org.example.codetodoapplicationpersonal.user.util.UserEntityDtoMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // interface for bCrypt
    private final JwtUtil jwtUtil;
    private final UnifiedNotificationDispatcher unifiedNotificationDispatcher;


    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UnifiedNotificationDispatcher unifiedNotificationDispatcher ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.unifiedNotificationDispatcher = unifiedNotificationDispatcher;
    }


    @Transactional
    public UserSignupResponseDTO signUp(String name, String username, String email, String password,UserRole userRole) throws UserAlreadyExistException {
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistException("User with username already exist");
        }
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistException("User with email already exist.Please Login instead or choose different Email ID");
        }
        User user = new User();
        user.setName(name);
//        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); //hash before setting
        user.setUserRole(userRole);
//        user.setUserStatus(UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);

        EmailRequestDTO emailRequestDTO = new EmailRequestDTO();
        emailRequestDTO.setTo(user.getEmail());
        emailRequestDTO.setSubject("Sign up successful !");
        emailRequestDTO.setContent("Hi "+user.getName()+ " , Welcome to Ecommerce Application ! Your account has been created successfully. Please login to continue.");
        unifiedNotificationDispatcher.dispatch(emailRequestDTO);

        return UserEntityDtoMapper.toUserSignupResponseDto(savedUser);

    }



    @Transactional
    public AuthLoginResponseDTO login(String username, String email, String password) throws UserNotFoundException {
        Optional<User> optionalUser= Optional.empty();

        if(username!=null && !username.isBlank()) {
            optionalUser= userRepository.findByUsername(username);
            if (optionalUser.isEmpty()) {
                throw new UserNotFoundException("User with username not found");
            }
        }
        else if(email!=null && !email.isBlank()) {
            optionalUser= userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                throw new UserNotFoundException("User with email not found");
            }
        }
        else { // case already handled in controller
            throw new InvalidCredentialsException("Username or Email must be provided");
        }

        User user = optionalUser.get();

        //check password match
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username/email or password");
        }


        //generating jwt token
        String jwtToken = jwtUtil.generateToken(user.getEmail(), user.getUserRole().name());
        UserLoginResponseDTO userLoginResponseDto = UserEntityDtoMapper.toUserLoginResponseDto(user);

        AuthLoginResponseDTO authLoginResponseDto = new AuthLoginResponseDTO(jwtToken, userLoginResponseDto);

        return authLoginResponseDto;

    }





//    @Transactional
//    public AuthLoginResponseDTO login(String username, String email, String password) throws UserNotFoundException {
//
//        Optional<User> optionalUserByUsername = userRepository.findByUsername(username);
//        if (optionalUserByUsername.isEmpty()) {
//            throw new UserNotFoundException("User with username not found");
//        }
//        User user = optionalUserByUsername.get();
//
//        //check password match
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            throw new InvalidCredentialsException("Invalid username or password");
//        }
//
//
//        //generating jwt token
//        String jwtToken = jwtUtil.generateToken(user.getUsername(), user.getUserRole().name());
//        UserLoginResponseDTO userLoginResponseDto = UserEntityDtoMapper.toUserLoginResponseDto(user);
//
//        AuthLoginResponseDTO authLoginResponseDto = new AuthLoginResponseDTO(jwtToken, userLoginResponseDto);
//
//        return authLoginResponseDto;
//
//    }

}