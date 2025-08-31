package com.example.ecommerce_springboot.ecommerce.controller;


import com.example.ecommerce_springboot.ecommerce.exceptions.ProductNotFoundException;
import com.example.ecommerce_springboot.ecommerce.models.Product;
import com.example.ecommerce_springboot.ecommerce.services.ProductService;
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


    private ProductService productService; //this just creates a variable of type ProductService , helps to link Controller with interface methods using Spring DI


    //Spring injects the dependency of ProductService using the constructor
    public ProductController(@Qualifier("SelfProductService") ProductService productService) {
        this.productService = productService;
    }


    /// ------------------------------------------------------------ SELLER specific APIs ------------------------------------------------------------------------------
    @PreAuthorize("hasRole('SELLER')")
    @PostMapping(value = "/sellers/products")
    public Product createProductForSeller(@RequestBody Product product)// RequestBody tells Controller to create product based on requestbody format in (fakestore) dto
    {
        Product p = productService.createProduct(product.getId(), product.getTitle(), product.getDescription(), product.getPrice(), product.getImageUrl(), product.getCategory().getTitle());
        return p;
    }


    //get Single Product api below -Todo- can remove if getAll filtered is done
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping(value = "/sellers/products/{id}")
    public ResponseEntity<Product> getProductByIdForSeller(@PathVariable("id") Long id) throws ProductNotFoundException {
        System.out.println("Starting API here");
        Product p = productService.getSingleProduct(id);
        System.out.println("Ending API here");

        ResponseEntity<Product> response = new ResponseEntity<>(p, HttpStatus.OK);// includes body p( ie product) and inbuilt status class

        return response;
    }


    // update product api for seller
    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping(value = "/sellers/products/{id}")
    public Product updateProductForSeller(@RequestBody Product product) //RequestBody is the body received by client which includes the changes
    {
        System.out.println("Updating product");
        Product p = productService.updateProduct(product.getId(), product.getTitle(), product.getDescription(), product.getPrice(), product.getImageUrl(), product.getCategory().getTitle());
        System.out.println("Updated product, closing API");
        return p;
    }


    //delete product api for seller
    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping(value = "/sellers/products/{id}")
    public Product deleteProductForSeller(@PathVariable("id") Long id) throws ProductNotFoundException {
        System.out.println("Product deleting");
        Product p = productService.deleteProduct(id);
        System.out.println("Product deleted");
        return p;  // Return info of product deleted as output as received from fakestore
    }


    // get all products for seller- Todo  convertto only one filtered getALl
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping(value = "sellers/products")
    public List<Product> getAllProductsForSeller() {
        System.out.println("Getting all products");
        List<Product> p = productService.getAllProducts();
        System.out.println("Ending API here, returned list on client side.");
        return p;
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/sellers/products-filtered")
    // Pagination for getAll products  // pageSize, pageNumber, fieldName, other example- sortOrder
    public Page<Product> getAllProductsFilteredForSeller(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize, @RequestParam("fieldName")  String fieldName){

//        return productService.getAllProductsByPage(pageNumber, pageSize, fieldName);

    return null;
    }


    /// ------------------------------------------------ Customer specific APIs ----------------------------------------------------------------------


    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customers/products")
    public Page<Product> getAllProductsByPageForCustomer(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {

        return null;

    }


    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customers/product-details")
    public Page<Product> getProductDetailsByIdForCustomer(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {

        return null;

    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customers/add-to-cart")
    public Page<Product> addProductToCartForCustomer(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {

        return null;

    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customers/cart")
    public Page<Product> getCartForCustomer(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {
        return null;
    }



    ///    ---------------------------------------------------ADMIN APIs---------------------------------------------------------------------------------------------


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/inventory-details")
    public Page<Product> getInventoryProductsForAdmin(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {

        return null;

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/order-id-details")
    public Page<Product> getOrderDetailsByOrderIDForAdmin(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {

        return null;
    }
}