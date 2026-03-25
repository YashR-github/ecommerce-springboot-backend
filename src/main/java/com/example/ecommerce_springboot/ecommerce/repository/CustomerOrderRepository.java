package com.example.ecommerce_springboot.ecommerce.repository;

import com.example.ecommerce_springboot.ecommerce.models.CustomerOrder;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
       select o from CustomerOrder o
       where o.id = :orderId
         and o.isDeleted = false
       """)
    Optional<CustomerOrder> findByIdForPayment(Long orderId);


}
