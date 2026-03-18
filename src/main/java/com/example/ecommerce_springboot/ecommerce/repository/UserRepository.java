package com.example.ecommerce_springboot.ecommerce.repository;

import com.example.ecommerce_springboot.ecommerce.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ecommerce_springboot.ecommerce.enums.UserRole;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdAndUserRoleAndIsDeletedFalse(Long userId, UserRole role);
    Optional<User> findByPhoneAndIsDeletedFalse(String phone);
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    boolean existsByPhoneAndIsDeletedFalse(String phone);
    boolean existsByEmailAndIsDeletedFalse(String email);

}
