package com.example.ecommerce_springboot.ecommerce.repository;

import com.example.ecommerce_springboot.ecommerce.models.Product;
import com.example.ecommerce_springboot.ecommerce.models.User;
import com.example.ecommerce_springboot.ecommerce.repository.projections.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> /* format- < Model, Data_Type of Model's Primary Key>   */ {

    // Paging query in jpa, uses page number and pageSize to do the correct limit and offset in order to return result
    @Override
    Page<Product> findAll(Pageable pageable) ;

    Page<Product> findAllByUserId(Long userId, Pageable pageable);

    Page<Product> findAllByCategory_Id(Long categoryId, Pageable pageable);// maybe be used by customer

    Optional<Product> findByUserAndTitle(User user, String title);

    Optional<Product> findByUserAndId(User user , Long id);




    //This will insert product records in my Product table
    Product save(Product product);

    //(Allows Searching in database using title value) Performs operation Select * from Product where Product.product_title=title
    Product findByTitle(String title);

    //(Allows Searching in database using description value) Performs operation Select * from Product where Product.Product_description=description
    Product findByDescription(String description);

    // Implement HQL(Hibernate query language): Find all products having a category as the category name(string) provided:
    @Query("select p from Product p where p.category.id=: categoryId")
    List<Product> getProductByCategoryId(@Param("categoryId") Long categoryId);

    // Implement Native Query : Find all products having a category as the category name(string) provided:
    @Query(value="select * from product p where p.category_id=: categoryId",nativeQuery=true)
    List<Product> getProductByCategoryIdNativeQuery(@Param("categoryId") Long categoryId);

    // Projections- Return specific columns instead of complete table
    @Query("select p.title as title, p.id as id from Product p where p.category.id=: categoryId")
    List<ProductProjection> getProductByCategoryIdUsingProjections(@Param("categoryId") Long categoryId);
}








// Note: JPQL is the most common way to perform database operations in Spring Data JPA.
// Note: Native Query is not recommended to be used as it is not type safe and can lead to SQL Injection attacks.
// It is recommended to use JPQL instead.
// Note: HQL requires class name Product, while Native query requires the table name/ attribute name product.

