package com.example.ecommerce_springboot.auth.service;




import com.example.ecommerce_springboot.auth.exceptions.InvalidCredentialsException;
import com.example.ecommerce_springboot.auth.exceptions.UserNotFoundException;
import com.example.ecommerce_springboot.auth.util.AuthenticatedUserUtil;
import com.example.ecommerce_springboot.ecommerce.enums.UserRole;
import com.example.ecommerce_springboot.ecommerce.models.User;
import com.example.ecommerce_springboot.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  AuthenticatedUserUtil authenticatedUserUtil;


    //delete user

    public void deleteUser(Long userId){
       User user=   authenticatedUserUtil.getCurrentUser();
        if(user.getUserRole()== UserRole.ADMIN)
            userRepository.deleteById(userId);
       if(!user.getId().equals(userId)){
           throw new InvalidCredentialsException("You are not authorized to delete this user");
       }
       if(!userRepository.existsById(userId)){
           throw new UserNotFoundException("User does not exist");
       }
        userRepository.deleteById(userId);

    }

}
