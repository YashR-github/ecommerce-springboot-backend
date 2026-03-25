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

    Optional<InventoryItem> findTopByReservedCartItem_IdAndItemStatusOrderByIdDesc(Long cartItemId, InventoryItemStatus status);


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
    @Query(value = """
        update inventory_item
        set item_status = 'AVAILABLE',
            reserved_cart_item_id = null,
            reservation_expiry_time = null
        where id = :inventoryItemId
        and item_status = 'RESERVED_IN_CART'
        """, nativeQuery = true)
    int releaseCartReservationForInventory(Long inventoryItemId);


    @Query("""
        select i from InventoryItem i
        where i.reservedCartItem.id = :cartItemId
        and i.itemStatus = 'RESERVED_IN_CART'
        order by i.id desc
        """)
    InventoryItem findReservedItemForCartItem(Long cartItemId);


    @Query(value = """
        select count(*)
        from inventory_item i
        join cart_item ci on ci.id = i.reserved_cart_item_id
        where ci.cart_id = :cartId
        and i.item_status = 'RESERVED_IN_CART'
        and i.reservation_expiry_time < :now
        """, nativeQuery = true)
    int countExpiredReservationsForCart(Long cartId, LocalDateTime now);

    @Modifying
    @Query(value = """
        update inventory_item
        set item_status = 'RESERVED_FOR_ORDER',
            reserved_order_id = :orderId,
            reservation_expiry_time = :expiry
        where reserved_cart_item_id = :cartItemId
        and item_status = 'RESERVED_IN_CART'
        """, nativeQuery = true)
    void freezeInventoryForOrder(Long orderId, Long cartItemId, LocalDateTime expiry);


    @Query(value = """
        select count(*)
        from inventory_item
        where reserved_order_id = :orderId
        and item_status = 'RESERVED_FOR_ORDER'
        and reservation_expiry_time < :now
        """, nativeQuery = true)
    int countExpiredOrderReservations(Long orderId, LocalDateTime now);


    @Modifying
    @Query(value = """
        update inventory_item
        set item_status = 'AVAILABLE',
            reserved_order_id = null,
            reservation_expiry_time = null
        where reserved_order_id = :orderId
        and item_status = 'RESERVED_FOR_ORDER'
        """, nativeQuery = true)
    void releaseOrderReservations(Long orderId);


    @Modifying (clearAutomatically = true, flushAutomatically = true)
    @Query(value= """
            update inventory_item
            set item_status = 'SOLD'
                reserved_order_id= null,
                reservation_expiry_time = null
            where reserved_order_id = :orderId
                  and item_status = 'RESERVED_FOR_ORDER'
            """, nativeQuery= true)
    void markInventorySold(Long orderId);




    @Query(value = """
            SELECT * FROM inventory_item
            WHERE product_listing_id = :listingId
            AND item_status = 'RESERVED_IN_CART'
            ORDER BY updated_at DESC
            LIMIT 1
            """, nativeQuery = true)
    InventoryItem findRecentlyReserved(Long listingId);



}




