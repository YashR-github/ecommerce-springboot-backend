package com.example.ecommerce_springboot.ecommerce.services;

import com.example.ecommerce_springboot.auth.util.AuthenticatedUserUtil;
import com.example.ecommerce_springboot.ecommerce.exceptions.ProductNotFoundException;
import com.example.ecommerce_springboot.ecommerce.exceptions.SellerProductAlreadyExistException;
import com.example.ecommerce_springboot.ecommerce.models.Category;
import com.example.ecommerce_springboot.ecommerce.models.Product;
import com.example.ecommerce_springboot.ecommerce.models.User;
import com.example.ecommerce_springboot.ecommerce.repository.CategoryRepository;
import com.example.ecommerce_springboot.ecommerce.repository.ProductRepository;
import com.example.ecommerce_springboot.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("SellerProductService")
@PreAuthorize("hasAuthority('SELLER')")
public class SellerProductService {

    private UserRepository userRepository;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private AuthenticatedUserUtil authenticatedUserUtil;

    public SellerProductService(UserRepository userRepository, ProductRepository productRepository, CategoryRepository categoryRepository, AuthenticatedUserUtil authenticatedUserUtil) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.authenticatedUserUtil = authenticatedUserUtil;
    }


    //create product method for seller
    @Transactional
    public Product createProduct(String title, String description, Double price, String image, String category) {
        Optional<Product> optionalProduct =productRepository.findByUserAndTitle(getCurrentUser(), title);
        if(optionalProduct.isPresent()) {
            throw new SellerProductAlreadyExistException("Product with same title already exist.");
        }

    /*  1. Check if category is there in db
        2. If not there, create it and use it while saving product.
        3. If there, use it in product directly.                                       */
        Product p= new Product();
        Optional<Category> optionalCategory= categoryRepository.findByTitle(category); // input param have category as string which is title in Category table
        if(optionalCategory.isEmpty()){  //This means category is not present in db
            Category newCat = new Category();
            newCat.setTitle(category);
            Category newRow= categoryRepository.save(newCat);
            p.setCategory(newRow);
        }
        else {
            p.setCategory(optionalCategory.get()); //if category is present in db, then set it to product object
        }
        p.setTitle(title);
        p.setDescription(description);
        p.setPrice(price);
        p.setImageUrl(image);
        Product savedProduct= productRepository.save(p);

        return savedProduct; //return product object after saving to db.
    }


 // delete product method for seller
    @Transactional
    public void deleteProduct(Long id) throws ProductNotFoundException {

        Optional<Product> optionalProduct= productRepository.findByUserAndId(getCurrentUser(),id);
        if(optionalProduct.isEmpty()) {
            throw new ProductNotFoundException("Product not found with id "+id+". The product might have been deleted already.");
        }
        productRepository.delete(optionalProduct.get());
    }


    @Transactional
    public Product updateProduct(String title, String description, Double price, String image, String category) {
        return null;
    }


    // GetAll method for returning Paginated results
    public Page<Product> getAllProductsByPage(int pageNumber, int pageSize, String fieldName){
        //PageRequest's of method uses pagination values like pageNumber,pageSize and sorting order and  PageRequest.of() returns "Pageable" object
        Page<Product> products= productRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(fieldName).ascending())) ;
        return products;
    }
//
//    @Override
//    public Product getSingleProduct(long id) throws ProductNotFoundException {
//        //TODO check from Inventory table if product is present or not
//        Optional<Product> p= productRepository.findById(id);
//        if( p.isPresent()) {
//            return p.get();
//        }
//        throw new ProductNotFoundException("Product not found with id "+id);
//    }



    public List<Product> getAllProducts() {
        return List.of();
    }


    //helper methods
    public User getCurrentUser() {
        return authenticatedUserUtil.getCurrentUser();
    }

}
