package com.example.ecommerce_springboot.ecommerce.configs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


//@Component
@Configuration
public class ApplicationConfiguration {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
