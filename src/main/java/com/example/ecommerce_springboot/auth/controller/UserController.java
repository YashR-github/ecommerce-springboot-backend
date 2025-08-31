package com.example.ecommerce_springboot.auth.controller;



import com.example.ecommerce_springboot.auth.service.UserService;
import com.example.ecommerce_springboot.ecommerce.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user-management")
public class UserController {

@Autowired
private UserService userService;


// delete user
    @DeleteMapping ("/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);

    }

//----------------------------ADMIN only APIs--------------------------------



    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/seller-details")
    public Page<Product> getAllSellerInfoForAdmin(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {

        return null;

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customer-details")
    public Page<Product> getAllCustomerInfoForAdmin(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {

        return null;
    }

}


