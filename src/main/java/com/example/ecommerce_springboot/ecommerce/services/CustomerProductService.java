package com.example.ecommerce_springboot.ecommerce.services;


import com.example.ecommerce_springboot.auth.util.AuthenticatedUserUtil;
import com.example.ecommerce_springboot.ecommerce.exceptions.ProductNotFoundException;
import com.example.ecommerce_springboot.ecommerce.models.Cart;
import com.example.ecommerce_springboot.ecommerce.models.CartItem;
import com.example.ecommerce_springboot.ecommerce.models.Product;
import com.example.ecommerce_springboot.ecommerce.models.User;
import com.example.ecommerce_springboot.ecommerce.repository.CartRepository;
import com.example.ecommerce_springboot.ecommerce.repository.ProductRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@PreAuthorize("hasAuthority('CUSTOMER')")
public class CustomerProductService {
    private final AuthenticatedUserUtil authenticatedUserUtil;
    private ProductRepository productRepository;
    private CartRepository cartRepository;

    public CustomerProductService(AuthenticatedUserUtil authenticatedUserUtil, ProductRepository productRepository, CartRepository cartRepository) {
        this.authenticatedUserUtil = authenticatedUserUtil;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }


    // Cart services
    public void addToCart(Long productId) throws ProductNotFoundException {
     Optional<Product> optionalProduct = productRepository.findById(productId);
     if(optionalProduct.isEmpty()) {
         throw new ProductNotFoundException("Product not found with id "+productId);
     }
     Cart cart = cartRepository.findByUser(getCurrentUser());
     CartItem cartItem= new CartItem();
     cartItem.setProduct(optionalProduct.get());
     cartItem.setQuantity(cartItem.getQuantity()+1); // add to existing quantity
     cartItem.setTotalCartItemsPrice(cartItem.getProduct().getPrice()*cartItem.getQuantity());


     cartRepository.save();





    }


    public Product getSingleProductDetails(Long productId) throws ProductNotFoundException {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if(optionalProduct.isEmpty()) {
            throw new ProductNotFoundException("Product not found with id "+productId);
        }
        return optionalProduct.get();

    }



    //helper methods
    public User getCurrentUser() {
        return authenticatedUserUtil.getCurrentUser();
    }
}
