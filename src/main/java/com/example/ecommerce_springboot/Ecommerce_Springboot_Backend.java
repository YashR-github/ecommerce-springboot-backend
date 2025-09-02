package com.example.ecommerce_springboot;

import com.example.ecommerce_springboot.ecommerce.models.Product;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Ecommerce_Springboot_Backend {

    public static void main(String[] args) {
        SpringApplication.run(Ecommerce_Springboot_Backend.class, args);

        Product p =new Product();
        p.setId(1L);
        System.out.println("hi"+p);
    }

}
