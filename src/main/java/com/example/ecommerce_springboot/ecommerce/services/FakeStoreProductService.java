package com.example.ecommerce_springboot.ecommerce.services;
import com.example.ecommerce_springboot.ecommerce.dto.FakeStoreProductDto;
import com.example.ecommerce_springboot.ecommerce.exceptions.ProductNotFoundException;
import com.example.ecommerce_springboot.ecommerce.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Arrays;



@Service("FakeStoreProductService")
public class FakeStoreProductService  implements ProductService{


    //rest template dependency for connecting to third party fakestore
    private RestTemplate restTemplate;
    private RedisTemplate redisTemplate;

    public FakeStoreProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = new RedisTemplate();
    }


    // service crud methods

    //get single product- Uses Redis cache for caching the data
    @Override
    public Product getSingleProduct(long id) throws ProductNotFoundException {
        System.out.println("Inside FK Store product service");


        // First part is: assume it as table name "PRODUCTS"
        // Second part : hashKey of the product
        // the method returns Object which is type casted/parsed to Product object
        Product redisProduct = (Product) redisTemplate.opsForHash().get("PRODUCTS","PRODUCTS_"+id);

        if(redisProduct!=null) {
            return redisProduct;
        }

        //else get from fakestore
        FakeStoreProductDto fakeStoreProductDto= restTemplate.getForObject("https://fakestoreapi.com/products/"+id, FakeStoreProductDto.class);

        if(fakeStoreProductDto==null){
            throw new ProductNotFoundException("Product not found with id "+id);
        }

        // put in Redis for caching for future use
        //in "PRODUCTS" table put key="PRODUCTS_"+id and value= the product from fakestore
        redisTemplate.opsForHash().put("PRODUCTS", "PRODUCTS_"+id, fakeStoreProductDto.getProduct());

        return fakeStoreProductDto.getProduct();
    }


// get all method
    @Override
    public List<Product> getAllProducts() {
        FakeStoreProductDto[] fakeStoreProductDto= restTemplate.getForObject("https://fakestoreapi.com/products",  FakeStoreProductDto[].class);
        //Convert each FakeStoreProductDto to Product and return as a List
        return Arrays.stream(fakeStoreProductDto).map(FakeStoreProductDto::getProduct).toList();

    }


    // create product method
    @Override
    public Product createProduct(Long id, String title, String description, Double price, String image, String category) {
        // instead of passing requestDTo as single parameter above, passing individual attributes is recommended to avoid tight coupling
        FakeStoreProductDto fakeStoreProductDto =new FakeStoreProductDto();
        fakeStoreProductDto.setId(id);
        fakeStoreProductDto.setTitle(title);
        fakeStoreProductDto.setDescription(description);
        fakeStoreProductDto.setPrice(price);
        fakeStoreProductDto.setImage(image);
        fakeStoreProductDto.setCategory(category);

        FakeStoreProductDto response= restTemplate.postForObject("https://fakestoreapi.com/products", fakeStoreProductDto, FakeStoreProductDto.class);

        return response.getProduct();
    }

    // delete (Single) Product
    public Product deleteProduct(Long id) throws ProductNotFoundException{
        //gets and checks the product exist or not
        FakeStoreProductDto fakeStoreProductDto= restTemplate.getForObject("https://fakestoreapi.com/products/"+id, FakeStoreProductDto.class);
        if (fakeStoreProductDto==null){
         throw new ProductNotFoundException("Product might be already deleted");
        }
        restTemplate.delete("https://fakestoreapi.com/products/"+id, FakeStoreProductDto.class);// deletes the product


        System.out.println(fakeStoreProductDto.toString()); // This prints output in console
        return fakeStoreProductDto.getProduct();
    }



    public Product updateProduct(Long id, String title, String description, Double price, String image, String category) {
        FakeStoreProductDto fakeStoreProductDto2= new FakeStoreProductDto();
        fakeStoreProductDto2.setId(id);
        fakeStoreProductDto2.setTitle(title);
        fakeStoreProductDto2.setDescription(description);
        fakeStoreProductDto2.setPrice(price);
        fakeStoreProductDto2.setImage(image);
        fakeStoreProductDto2.setCategory(category);

        restTemplate.put("https://fakestoreapi.com/products/"+id,  fakeStoreProductDto2);
        return fakeStoreProductDto2.getProduct();
    }



    // Todo implement pagination using third party fakestore
    // GetAll method for returning Paginated results
    @Override
    public Page<Product> getAllProductsByPage(int pageNumber, int pageSize, String fieldName){
//        //PageRequest's of method uses pagination values like pageNumber,pageSize and sorting order and  PageRequest.of() returns "Pageable" object
//        Page<Product> products= restTemplate.getForObject(PageRequest.of(pageNumber, pageSize, Sort.by(fieldName).ascending())) ;
//        return products;
        return null;
    }








}
