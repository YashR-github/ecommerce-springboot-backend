package com.example.ecommerce_springboot.ecommerce.services;

import com.example.ecommerce_springboot.ecommerce.exceptions.ProductNotFoundException;
import com.example.ecommerce_springboot.ecommerce.models.Product;
import org.springframework.data.domain.Page;

import java.util.List;

// Interface having methods that can be used as common for different services
public interface ProductService {
    Product getSingleProduct (long id) throws ProductNotFoundException;
    List<Product> getAllProducts();
    Product createProduct(Long id, String title, String description, Double price, String image, String category);
    Product deleteProduct(Long id) throws ProductNotFoundException;
    Product updateProduct(Long id, String title, String description, Double price, String image, String category);
    Page<Product> getAllProductsByPage(int pageNumber, int pageSize, String fieldName);
}
