package com.example.ecommerce_springboot.ecommerce.repository;


import com.example.ecommerce_springboot.ecommerce.enums.CartItemStatus;
import com.example.ecommerce_springboot.ecommerce.models.Cart;
import com.example.ecommerce_springboot.ecommerce.models.CartItem;
import com.example.ecommerce_springboot.ecommerce.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

//    Optional<CartItem> findByIdAndIsDeletedFalseAndCart_User(Long cartItemId, User user);
    Page<CartItem> findByCart_IdAndIsDeletedFalseAndCartItemStatusIn(Long cartId, List<CartItemStatus> cartItemStatuses, Pageable pageable);
    Optional<CartItem> findByIdAndCart_UserAndQuantityGreaterThanAndIsDeletedFalseAndCartItemStatusIn(Long cartItemId, User user, Integer quantity, List<CartItemStatus> cartItemStatuses);
    Page<CartItem> findAllByCart_IdAndIsDeletedFalse(Long cartId, Pageable pageable);
}
