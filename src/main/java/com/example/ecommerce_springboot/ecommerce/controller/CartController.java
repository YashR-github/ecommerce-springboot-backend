package com.example.ecommerce_springboot.ecommerce.controller;


import com.example.ecommerce_springboot.ecommerce.dto.CartItemDTO;
import com.example.ecommerce_springboot.ecommerce.services.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/customers/cart")
public class CartController {
    private final CustomerService customerService;

    public CartController(CustomerService customerService) {
        this.customerService = customerService;
    }



// ------------------------------------------------ Customer specific APIs ----------------------------------------------------------------------

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public ResponseEntity<Page<CartItemDTO>> getCartByPage(@PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable)
    {  Page<CartItemDTO> cartItemDTOS = customerService.getCustomerCart(pageable);
        return new ResponseEntity<>(cartItemDTOS, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/add-item/{productListingId}")
    public ResponseEntity<Page<CartItemDTO>> addItemToCart(@PathVariable Long productListingId, @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)  Pageable pageable) {
        Page<CartItemDTO> cartPage = customerService.addItemToCart(productListingId,pageable);
        return ResponseEntity.ok(cartPage);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/remove-item/{cartItemId}")
    public ResponseEntity<Page<CartItemDTO>> removeItemFromCart(@PathVariable Long cartItemId, @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)  Pageable pageable) {
        Page<CartItemDTO> cartItemDTOs = customerService.removeFromCart(cartItemId,pageable);
        return ResponseEntity.ok(cartItemDTOs);
    }


}
