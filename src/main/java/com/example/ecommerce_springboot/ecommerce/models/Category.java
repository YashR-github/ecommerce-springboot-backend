package com.example.ecommerce_springboot.ecommerce.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Category extends BaseModel{
    private String title;

    @JsonIgnore
    @OneToMany(mappedBy = "category",fetch = FetchType.LAZY)
    private List<Product> products;
}

