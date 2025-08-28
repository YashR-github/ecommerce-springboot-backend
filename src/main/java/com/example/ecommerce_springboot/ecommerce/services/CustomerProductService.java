package com.example.ecommerce_springboot.ecommerce.services;


import com.example.ecommerce_springboot.auth.util.AuthenticatedUserUtil;
import com.example.ecommerce_springboot.ecommerce.models.Product;
import com.example.ecommerce_springboot.ecommerce.models.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerProductService {
    private final AuthenticatedUserUtil authenticatedUserUtil;

    public CustomerProductService(AuthenticatedUserUtil authenticatedUserUtil) {
        this.authenticatedUserUtil = authenticatedUserUtil;
    }


    // Cart services
    public void addToCart(Product product){
//        get the current user
        User user= authenticatedUserUtil.getCurrentUser();






    }




}
