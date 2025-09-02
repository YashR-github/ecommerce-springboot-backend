package com.example.ecommerce_springboot.ecommerce.services;

import com.example.ecommerce_springboot.ecommerce.exceptions.ProductNotFoundException;
import com.example.ecommerce_springboot.ecommerce.models.Category;
import com.example.ecommerce_springboot.ecommerce.models.Product;
import com.example.ecommerce_springboot.ecommerce.repository.CategoryRepository;
import com.example.ecommerce_springboot.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("AdminProductService")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminProductService {





    //ADMIN specific methods







}
