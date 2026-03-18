package com.example.ecommerce_springboot.auth.util;



import com.example.ecommerce_springboot.auth.exceptions.InvalidCredentialsException;
import com.example.ecommerce_springboot.auth.exceptions.UnauthorizedAccessException;
import com.example.ecommerce_springboot.auth.exceptions.UserNotFoundException;
import com.example.ecommerce_springboot.ecommerce.enums.UserRole;
import com.example.ecommerce_springboot.ecommerce.models.User;
import com.example.ecommerce_springboot.ecommerce.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserUtil { //extracts user from the security context

    private final UserRepository userRepository;

    public AuthenticatedUserUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser(){
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();

        if(auth==null || !auth.isAuthenticated() || auth.getName()==null) {
            throw new UserNotFoundException("Authentication failed or token expired. Please login again.");
        }
        long userId;
        try{
            userId= Long.parseLong(auth.getName());
        }
        catch(NumberFormatException e){
            throw new UnauthorizedAccessException("Invalid authentication token.");
        }
        return userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(()-> new UserNotFoundException("User account no longer exist. The user may have been deleted."));
    }

    public UserRole getCurrentUserRole(String email, String phone){
        User user;
        if((email==null || email.isBlank()) && (phone!=null && !phone.isBlank())){
            user = userRepository.findByPhoneAndIsDeletedFalse(phone).orElseThrow(()-> new InvalidCredentialsException("Invalid credentials provided."));
        }
        else if((email!=null && !email.isBlank()) && (phone==null || phone.isBlank())){user = userRepository.findByEmailAndIsDeletedFalse(email).orElseThrow(()-> new InvalidCredentialsException("Invalid credentials provided."));
        }
        else{
            throw new InvalidCredentialsException("Invalid credentials provided.");
        }
        return user.getUserRole();
    }
}
