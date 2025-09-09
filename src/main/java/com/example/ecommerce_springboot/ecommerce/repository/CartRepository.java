package com.example.ecommerce_springboot.ecommerce.repository;

import com.example.ecommerce_springboot.ecommerce.models.Cart;
import com.example.ecommerce_springboot.ecommerce.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long>{

    Cart findByUser(User user);
}
