package com.example.ecommerce_springboot.ecommerce.repository.projections;

// act as a dto for projection
public interface ProductProjection {
    // here instead of attributes we define getters for projections
    Long getId();
    String getTitle();
}
