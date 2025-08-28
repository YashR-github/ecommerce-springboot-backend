package com.example.ecommerce_springboot.ecommerce.models;


import com.example.ecommerce_springboot.ecommerce.enums.CartStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Cart extends BaseModel {

    @OneToOne
    private User user;

    @OneToMany(mappedBy = "cart",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CartItem> cartItems= new ArrayList<>();


    @Enumerated(EnumType.STRING)
    private CartStatus status=CartStatus.EMPTY;

}





