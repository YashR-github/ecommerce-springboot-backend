package com.example.ecommerce_springboot.auth.util;


import com.example.ecommerce_springboot.auth.service.AuthAdminService;
import com.example.ecommerce_springboot.auth.service.AuthCustomerService;
import com.example.ecommerce_springboot.auth.service.AuthSellerService;
import com.example.ecommerce_springboot.auth.service.AuthService;
import com.example.ecommerce_springboot.ecommerce.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceResolver {

    private final AuthAdminService authAdminService;
    private final AuthCustomerService authCustomerService;
    private final AuthSellerService authSellerService;

    public AuthServiceResolver(AuthAdminService authAdminService, AuthCustomerService authCustomerService, AuthSellerService authSellerService) {
        this.authAdminService = authAdminService;
        this.authCustomerService = authCustomerService;
        this.authSellerService = authSellerService;
    }

    public AuthService getService(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }
        return switch (role) {
            case ADMIN -> authAdminService;
            case CUSTOMER -> authCustomerService;
            case SELLER -> authSellerService;
            default -> throw new IllegalArgumentException("No service found for role: " + role);
        };
    }
}
