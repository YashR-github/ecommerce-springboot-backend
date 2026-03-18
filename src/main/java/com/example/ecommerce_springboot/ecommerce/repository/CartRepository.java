package com.example.ecommerce_springboot.ecommerce.repository;

import com.example.ecommerce_springboot.ecommerce.enums.CartStatus;
import com.example.ecommerce_springboot.ecommerce.models.Cart;
import com.example.ecommerce_springboot.ecommerce.models.CartItem;
import com.example.ecommerce_springboot.ecommerce.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long>{

    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUserAndIsDeletedFalseAndCartStatusNotIn(User user, List<CartStatus> cartStatuses);

}
