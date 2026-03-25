package com.example.ecommerce_springboot.ecommerce.controller;


import com.example.ecommerce_springboot.ecommerce.dto.*;
import com.example.ecommerce_springboot.ecommerce.exceptions.ProductNotFoundException;
import com.example.ecommerce_springboot.ecommerce.models.Product;
import com.example.ecommerce_springboot.ecommerce.services.AdminService;
import com.example.ecommerce_springboot.ecommerce.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final AdminService adminService;
    private final CustomerService customerService;

    public ProductController(AdminService adminService, CustomerService customerService) {
        this.adminService = adminService;
        this.customerService= customerService;
    }

// --------------------------------------------Customer APIs----------------------------------------------------------------------------------------------------------

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customers/homepage")
    public ResponseEntity<List<HomePageProductDTO>> getCatalogueHomePageProducts(){
        List<HomePageProductDTO> catalogueProducts = customerService.getHomePageProductsDisplay();
        return ResponseEntity.ok(catalogueProducts);
    }

//---------------------------------------------------------ADMIN APIs-------------------------------------------------------------------------------------------------


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/admin/create/product")
    public ResponseEntity<ProductCreateResponseDto> createNewProduct(@RequestBody NewProductCreateReqDTO productReqDto)
    {   ProductCreateResponseDto productListingResDto = adminService.createNewProduct(productReqDto.getName(),productReqDto.getShortDescription(),productReqDto.getLongDescription(),productReqDto.getBrand(),productReqDto.getModel(), productReqDto.getWeightInGrams(), productReqDto.getBasePrice(), productReqDto.getBaseImageUrl(),productReqDto.getCategory().toString().toUpperCase());
        return new ResponseEntity<>(productListingResDto,HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/admin/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Long productToUpdateId , @RequestBody ProductUpdateReqDTO productDto)
    {  Product product = adminService.updateProduct(productToUpdateId, productDto.getTitle(), productDto.getShortDescription(), productDto.getLongDescription(), productDto.getBrand(),  productDto.getWeightInGrams(), productDto.getBaseImageUrl(), productDto.getCategoryId(),productDto.getProductStatus());
        return new ResponseEntity<>(product,HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/admin/delete/product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) throws ProductNotFoundException {
        adminService.markProductDeletion(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}