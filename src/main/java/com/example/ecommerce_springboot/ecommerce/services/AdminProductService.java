package com.example.ecommerce_springboot.ecommerce.services;

import com.example.ecommerce_springboot.ecommerce.exceptions.ProductNotFoundException;
import com.example.ecommerce_springboot.ecommerce.models.Category;
import com.example.ecommerce_springboot.ecommerce.models.Product;
import com.example.ecommerce_springboot.ecommerce.repository.CategoryRepository;
import com.example.ecommerce_springboot.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("SelfProductService")
public class AdminProductService implements ProductService {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;

    public AdminProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    //ADMIN specific methods
    @Override
    public Product getSingleProduct(long id) throws ProductNotFoundException {
        //TODO check from Inventory table if product is present or not
        Optional<Product> p= productRepository.findById(id);
        if( p.isPresent()) {
        return p.get();
        }
        throw new ProductNotFoundException("Product not found with id "+id);
    }


    @Override
    public List<Product> getAllProducts() {
        return List.of();
    }

    // GetAll method for returning Paginated results
    @Override
    public Page<Product> getAllProductsByPage(int pageNumber, int pageSize, String fieldName){
        //PageRequest's of method uses pagination values like pageNumber,pageSize and sorting order and  PageRequest.of() returns "Pageable" object
        Page<Product> products= productRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(fieldName).ascending())) ;
        return products;
    }




    @Override
    public Product createProduct(Long id, String title, String description, Double price, String image, String category) {
//        1. Check if category is there in db
//        2. If not there, create it and use it while saving product.
//        3. If there, use it in product directly.
        Product p= new Product();
        Category currentCategory= categoryRepository.findByTitle(category); // input param have category as string which is title in Category table
        if(currentCategory==null){ // can also implement using optional.isEmpty, in repo it should then be defined as Optional return
            //This means category is not present in db
            Category newCat = new Category();
            newCat.setTitle(category);
            Category newRow= categoryRepository.save(newCat);
            p.setCategory(newRow);
        }
        else {
           p.setCategory(currentCategory); //if category is present in db, then set it to product object
        }

        p.setTitle(title);
        p.setDescription(description);
        p.setPrice(price);
        p.setImageUrl(image);
        Product savedProduct= productRepository.save(p);

        return savedProduct; //return product object after saving to db.
    }

    @Override
    public Product deleteProduct(Long id) throws ProductNotFoundException {
        return null;
    }

    @Override
    public Product updateProduct(Long id, String title, String description, Double price, String image, String category) {
        return null;
    }
}
