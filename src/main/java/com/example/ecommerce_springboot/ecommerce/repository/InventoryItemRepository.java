package com.example.ecommerce_springboot.ecommerce.repository;

import com.example.ecommerce_springboot.ecommerce.enums.InventoryItemStatus;
import com.example.ecommerce_springboot.ecommerce.enums.ItemStatus;
import com.example.ecommerce_springboot.ecommerce.models.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    List<InventoryItem> findByProductListing_IdAndItemStatusInAndIsDeletedFalse(Long productListing_id, List<InventoryItemStatus> itemStatuses);

    List<InventoryItem> findByProductListing_IdAndIsDeletedFalse(Long listingId);

    @Modifying
    @Query(value = """
            UPDATE inventory_item
            SET item_status = 'RESERVED_IN_CART'
             WHERE id = (
                            SELECT id FROM inventory_item
                            WHERE product_listing_id = :listingId
                            AND item_status = 'AVAILABLE'
                            LIMIT 1)

""", nativeQuery =true)
    int reserveOne(@Param("listingId") Long listingId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update inventory_item
        set item_status = 'RESERVED_IN_CART',
            reserved_cart_item_id = :cartItemId,
            reservation_expiry_time = :expiry
        where id = (
            select id from inventory_item
            where product_listing_id = :listingId
            and item_status = 'AVAILABLE'
            order by id
            limit 1
        )
        """, nativeQuery = true)
    int reserveOneForCart(Long listingId, Long cartItemId, LocalDateTime expiry);



    @Modifying
    @Query("""
        update InventoryItem i
        set i.itemStatus = 'RESERVED_FOR_ORDER'
                where i.id= :id
                 and i.itemStatus ='RESERVED_IN_CART'
       """)
           int reserveForOrder(Long id);







    @Query(value = """
            SELECT * FROM inventory_item
            WHERE product_listing_id = :listingId
            AND item_status = 'RESERVED_IN_CART'
            ORDER BY updated_at DESC
            LIMIT 1
            """, nativeQuery = true)
    InventoryItem findRecentlyReserved(Long listingId);



}




