package com.example.ecommerce_springboot.auth.service;


import com.example.ecommerce_springboot.auth.dtos.AuthLoginResponseDTO;
import com.example.ecommerce_springboot.auth.dtos.UserSignupResponseDTO;
import com.example.ecommerce_springboot.auth.exceptions.UserAlreadyExistException;
import com.example.ecommerce_springboot.auth.exceptions.UserNotFoundException;
import com.example.ecommerce_springboot.auth.util.UserEntityDtoMapper;
import com.example.ecommerce_springboot.ecommerce.enums.UserRole;
import com.example.ecommerce_springboot.ecommerce.models.User;
import com.example.ecommerce_springboot.ecommerce.repository.UserRepository;
import com.example.ecommerce_springboot.notifications.dispatch.UnifiedNotificationDispatcher;
import com.example.ecommerce_springboot.notifications.dtos.EmailRequestDTO;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthAdminService  implements AuthService{


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // for BCrypt
    private final UnifiedNotificationDispatcher unifiedNotificationDispatcher;
    private final AuthLoginService authLoginService;


    public AuthAdminService(UserRepository userRepository, PasswordEncoder passwordEncoder, UnifiedNotificationDispatcher unifiedNotificationDispatcher, AuthLoginService authLoginService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.unifiedNotificationDispatcher = unifiedNotificationDispatcher;
        this.authLoginService = authLoginService;
    }

    @Transactional
    public UserSignupResponseDTO signUp(String name, String phone, String email, String password, UserRole userRole) throws UserAlreadyExistException {
        if (userRepository.existsByPhoneAndIsDeletedFalse(phone)) {
            throw new UserAlreadyExistException("User with phone number already exist");
        }
        if (userRepository.existsByEmailAndIsDeletedFalse(email)) {
            throw new UserAlreadyExistException("User with email already exist.Please Login instead or choose different Email ID");
        }
        User user = new User();
        user.setName(name);
        user.setPhone(phone);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); //hash before setting
        user.setUserRole(userRole);
        User savedUser = userRepository.save(user);

        EmailRequestDTO emailRequestDTO = new EmailRequestDTO();
        emailRequestDTO.setTo(user.getEmail());
        emailRequestDTO.setSubject("Sign up successful !");
        emailRequestDTO.setContent("Hi "+user.getName()+ " , Welcome to CommerceHub Ecommerce-Application ! Your account has been created successfully. Please login to continue.");
        unifiedNotificationDispatcher.dispatch(emailRequestDTO);

        return UserEntityDtoMapper.toUserSignupResponseDto(savedUser);

    }


    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AuthLoginResponseDTO login(String phone, String email , String password) throws UserNotFoundException {
        return authLoginService.loginUser(phone, email, password);
    }

}
