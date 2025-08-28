//package com.example.ecommerce_springboot.controller;
//import com.example.ecommerce_springboot.dto.ErrorDto;
//import com.example.ecommerce_springboot.exceptions.ProductNotFoundException;
//import com.example.ecommerce_springboot.models.Product;
//import com.example.ecommerce_springboot.services.ProductService;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.data.domain.Page;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//
//
//@RestController
//public class ProductController {
//
//
//    private ProductService productService; ///this just creates a variable of type ProductService , helps to link Controller with interface methods using Spring DI
//
//
//    //Spring injects the dependency of ProductService using the constructor
//    public ProductController(@Qualifier("SelfProductService") ProductService productService) {
//        this.productService = productService;
//    }
//
//
//
///// SELLER specific APIs
//    //Create product api
//    // Alternate way for below line-->  @RequestMapping(value = "/products", method = RequestMethod.POST)
//    @PostMapping(value= "/products")
//    public Product createProduct(@RequestBody Product product)// RequestBody tells Controller to create product based on requestbody format in (fakestore) dto
//    {   // productService interface has method createProduct
//        Product p = productService.createProduct(product.getId(), product.getTitle(), product.getDescription(), product.getPrice(), product.getImageUrl(), product.getCategory().getTitle());
//        return p;
//    }
//
//
//
//    //get Single Product api below
//    @GetMapping(value = "/products/{id}")
//    /// Product is a model class that is used below as data type for p
//    ///ResponseEntity adds meta data on top of Product
//    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) throws ProductNotFoundException {
//        System.out.println("Starting API here");
//        Product p = productService.getSingleProduct(id);
//        System.out.println("Ending API here");
//
//        /// Since return type is ResponseEntity, we have to first create an object of it and pass Http Response class
//        /// already created by Spring for effective Status handling
//        ResponseEntity<Product> response= new ResponseEntity<>( p, HttpStatus.OK);// includes body p( ie product) and inbuilt status class
//
//        return response; // Returns the final output on client side
//    }
//
//
//
//
//    // update product api
//    @PutMapping (value = "/products/{id}")
//    public Product updateProduct(@RequestBody Product product) //RequestBody is the body received by client which includes the changes
//     {
//         System.out.println("Updating product");
//        Product p= productService.updateProduct(product.getId(), product.getTitle(), product.getDescription(), product.getPrice(), product.getImageUrl(), product.getCategory().getTitle());
//        System.out.println("Updated product, closing API");
//        return p;
//    }
//
//
//
//    //delete product api
//    @DeleteMapping(value = "/products/{id}")
//    public Product deleteProduct(@PathVariable("id") Long id) throws ProductNotFoundException {
//        System.out.println("Product deleting");
//        Product p = productService.deleteProduct(id);
//        System.out.println("Product deleted");
//        return p;  // Return info of product deleted as output as received from fakestore
//    }
//
//
//    @GetMapping(value ="/products")
//    public List<Product> getAllProducts() {
//        System.out.println("Getting all products");
//        List<Product> p=productService.getAllProducts();
//        System.out.println("Ending API here, returned list on client side.");
//        return p;
//    }
//
//    @GetMapping("/products")
//    // Pagination for getAll products  // pageSize, pageNumber, fieldName, other example- sortOrder
//    public Page<Product> getAllProductsByPage(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize, @RequestParam("fieldName")  String fieldName){
//        return productService.getAllProductsByPage(pageNumber, pageSize, fieldName);
//    }
//
//
//
//
//    //Controller Advice
//    ///Note: Exception e contains all the stack trace received on client side during error,
//    /// e.getMessage reduces the stack trace message to readable form
//    @ExceptionHandler(ProductNotFoundException.class) // annotation to invoke below method whenever error occurs in wherever parameter ProductNotFoundException.class class is thrown
//    /// Note: the parameter class above needs to be thrown as above in getProductById
//    public ResponseEntity<ErrorDto> handleProductNotFoundException(Exception e){
//        ErrorDto errorDto = new ErrorDto(); //creating an object of ErrorDto
//        errorDto.setMessage(e.getMessage()); // getMessage extracts only the readable message from stack trace message, setMessage sets the message to object errorDto
//        ResponseEntity<ErrorDto> response = new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
//        return response;
//    }
//
//
//
//}
//
//
//
