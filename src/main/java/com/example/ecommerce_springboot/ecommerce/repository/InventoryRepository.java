package com.example.ecommerce_springboot.ecommerce.repository;

import com.example.ecommerce_springboot.ecommerce.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
