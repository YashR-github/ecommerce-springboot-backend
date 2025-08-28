package com.example.ecommerce_springboot.ecommerce.models;


import com.example.ecommerce_springboot.ecommerce.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "users")
@Entity
public class User extends BaseModel{

    private String name;
    private String email;
    private String password;
    private UserRole userRole;

}
