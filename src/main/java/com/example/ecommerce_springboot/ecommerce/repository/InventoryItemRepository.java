package com.example.ecommerce_springboot.ecommerce.repository;

import com.example.ecommerce_springboot.ecommerce.models.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
}
