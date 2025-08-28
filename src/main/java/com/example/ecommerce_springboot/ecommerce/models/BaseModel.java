package com.example.ecommerce_springboot.ecommerce.models;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass  // tells Hibernate this is a superclass
public class BaseModel {
   @Id
   @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private Date createdAt;
    private Date updatedAt;
    private boolean isDeleted;


}



