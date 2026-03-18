package com.example.ecommerce_springboot.ecommerce.models;


import com.example.ecommerce_springboot.ecommerce.enums.CartStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Cart extends BaseModel {

    @Version
    private Long version;

    @OneToOne
    private User user;

    @OneToMany(mappedBy = "cart",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CartItem> cartItems= new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private CartStatus cartStatus=CartStatus.EMPTY;

    @PositiveOrZero
    private BigDecimal cartTotal= BigDecimal.ZERO;

}





