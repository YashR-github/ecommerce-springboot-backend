package com.example.ecommerce_springboot.ecommerce.controller;


import com.example.ecommerce_springboot.ecommerce.dto.CustomerOrderResponseDTO;
import com.example.ecommerce_springboot.ecommerce.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {



//---------------------------- Customer specific APIs ----------------------------------------------------------------------------

//    @PreAuthorize("hasRole('CUSTOMER')")
//    public Page<Product> getCustomerOrdersByPage(Pageable pageable) {
//
//    }

//@PreAuthorize("hasRole('CUSTOMER')")
//    public Page<Product> getCustomerOrderDetailsById(Pageable pageable) {
//
//    }

//    @PreAuthorize("hasRole('CUSTOMER')")
//    public CustomerOrderResponseDTO moveCartToOrderFlow() {
//
//    }






//------------------------- ADMIN specific APIs --------------------------------------------------------------------------------

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/orders/details/{id}")
    public Page<Product> getOrderDetailsByOrderID(Pageable pageable) {

        return null;
    }
}
