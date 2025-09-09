package com.example.ecommerce_springboot.ecommerce.controller;


import com.example.ecommerce_springboot.ecommerce.exceptions.ProductNotFoundException;
import com.example.ecommerce_springboot.ecommerce.models.Product;
import com.example.ecommerce_springboot.ecommerce.services.AdminProductService;
import com.example.ecommerce_springboot.ecommerce.services.CustomerProductService;
import com.example.ecommerce_springboot.ecommerce.services.ProductService;
import com.example.ecommerce_springboot.ecommerce.services.SellerProductService;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {


    private ProductService productService;
    private final SellerProductService sellerProductService;
    private final CustomerProductService customerProductService;
    private final AdminProductService adminProductService;


    //Spring injects the dependency of ProductService using the constructor
    public ProductController(@Qualifier("FakeStoreProductService") ProductService productService, SellerProductService sellerProductService, CustomerProductService customerProductService, AdminProductService adminProductService) {
        this.productService = productService;
        this.sellerProductService = sellerProductService;
        this.customerProductService = customerProductService;
        this.adminProductService = adminProductService;
    }


    /// ------------------------------------------------------------ SELLER specific APIs ------------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('SELLER')")
    @PostMapping(value = "/sellers/products")
    public ResponseEntity<Product> createProductForSeller(@RequestBody Product product)// RequestBody tells Controller to create product based on requestbody format in (fakestore) dto
    {   Product p = sellerProductService.createProduct(product.getTitle(), product.getDescription(), product.getPrice(), product.getImageUrl(), product.getCategory().getTitle());
        return new ResponseEntity<>(p,HttpStatus.OK);
    }


//    //get Single Product api below -Todo- can remove if getAll filtered is done
//    @PreAuthorize("hasAuthority('SELLER')")
//    @GetMapping(value = "/sellers/products/{id}")
//    public ResponseEntity<Product> getProductByIdForSeller(@PathVariable("id") Long id) throws ProductNotFoundException {
//        Product p = productService.getSingleProduct(id);
//        return new ResponseEntity<>(p, HttpStatus.OK);// includes body p( ie product) and inbuilt status class
//    }


    // update product api for seller
    @PreAuthorize("hasAuthority('SELLER')")
    @PatchMapping(value = "/sellers/products/{id}")
    public ResponseEntity<Product> updateProductForSeller(@RequestBody Product product) //RequestBody is the body received by client which includes the changes
    {
        Product p = sellerProductService.updateProduct(product.getTitle(), product.getDescription(), product.getPrice(), product.getImageUrl(), product.getCategory().getTitle());
        return new ResponseEntity<>(p,HttpStatus.OK);
    }


    //delete product api for seller
    @PreAuthorize("hasAuthority('SELLER')")
    @DeleteMapping(value = "/sellers/products/{id}")
    public ResponseEntity<Void> deleteProductForSeller(@PathVariable("id") Long id) throws ProductNotFoundException {

        sellerProductService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.OK);  // Return info of product deleted as output as received from fakestore
    }

//
//    // get all products for seller-
//    @PreAuthorize("hasAuthority('SELLER')")
//    @GetMapping(value = "sellers/products")
//    public List<Product> getAllProductsForSeller() {
//        List<Product> p = productService.getAllProducts();
//        return p;
//    } Todo  convert below api to only one filtered getALl

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping("/sellers/products-filtered")
    // Pagination for getAll products  // pageSize, pageNumber, fieldName, other example- sortOrder
    public Page<Product> getAllProductsFilteredForSeller(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize, @RequestParam("fieldName")  String fieldName){

//        return productService.getAllProductsByPage(pageNumber, pageSize, fieldName);

    return null;
    }


    /// ------------------------------------------------ Customer specific APIs ----------------------------------------------------------------------


    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/customers/products")
    public Page<Product> getAllFilteredProductsByPageForCustomer(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {

        return null;

    }


    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/customers/product-details/{product_id}")
    public Page<Product> getSingleProductDetailsByIdForCustomer(@PathVariable("product_id") Long productId) {
          Product p = customerProductService.getSingleProductDetails(productId);
        return null;

    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/customers/add-to-cart")
    public Page<Product> addProductToCartForCustomer(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {

        return null;

    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/customers/cart")
    public Page<Product> getCartByPageForCustomer(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {
        return null;
    }



    ///    ---------------------------------------------------ADMIN APIs---------------------------------------------------------------------------------------------


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/inventory-details")
    public Page<Product> getFilteredInventoryByPageForAdmin(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {

        return null;

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/order-id-details")
    public Page<Product> getOrderDetailsByOrderIDForAdmin(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {

        return null;
    }
}