package com.example.ecommerce_springboot.ecommerce.repository;

import com.example.ecommerce_springboot.ecommerce.enums.CartStatus;
import com.example.ecommerce_springboot.ecommerce.models.Cart;
import com.example.ecommerce_springboot.ecommerce.models.CartItem;
import com.example.ecommerce_springboot.ecommerce.models.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long>{

    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUserAndIsDeletedFalseAndCartStatusNotIn(User user, List<CartStatus> cartStatuses);



    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
       select c from Cart c
       where c.user = :user
       and c.cartStatus = :cart_status
       """)
    Optional<Cart> findActiveCartForCheckout(User user,CartStatus cart_status);

}
