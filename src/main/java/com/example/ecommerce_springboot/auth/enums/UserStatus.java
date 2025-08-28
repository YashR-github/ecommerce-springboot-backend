package com.example.ecommerce_springboot.auth.enums;

import com.example.ecommerce_springboot.ecommerce.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserStatus {
    // might not need it but added for now
    ACTIVE,
    INACTIVE ;

    @JsonCreator  //Automatically tells jackson to use this method when parsing json string to enum
    public static UserRole fromString(String role){
        return UserRole.valueOf(role.toUpperCase());
    }

}
