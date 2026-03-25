package com.example.ecommerce_springboot.ecommerce.repository;

import com.example.ecommerce_springboot.ecommerce.enums.ListingStatus;
import com.example.ecommerce_springboot.ecommerce.enums.ProductStatus;
import com.example.ecommerce_springboot.ecommerce.models.Product;
import com.example.ecommerce_springboot.ecommerce.models.ProductListing;
import com.example.ecommerce_springboot.ecommerce.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ProductListingRepository extends JpaRepository<ProductListing,Long> {


    @Query("""
        SELECT p1
        FROM ProductListing p1
        JOIN p1.product p
        WHERE
            p.productStatus = :productStatus
            AND p1.listingStatus = :listingStatus
        AND(
            LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%' ))
            OR LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p1.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p1.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )""")
    Page<ProductListing> searchListingsByKeyword(String keyword, ProductStatus productStatus, ListingStatus listingStatus, Pageable pageable);




    Optional<ProductListing> findByListingCreatorAndIdAndIsDeletedFalse(User seller, Long productListingId);

//    Optional<ProductListing> findById(Long productListingId);

    Boolean existsByProductAndListingCreatorAndListingStatusIn( Product product, User seller, List<ListingStatus> statuses);

//    Optional<ProductListing> findByIdAndIsDeletedFalse(Long productListingId);

//    Optional<ProductListing> findByIdAndListingStatusAndIsDeletedFalse(Long listingId, ListingStatus listingStatus);

   Optional<ProductListing> findByIdAndListingCreatorAndListingStatusAndIsDeletedFalse(Long listingId, User user, ListingStatus listingStatus);

    Optional<ProductListing> findByIdAndListingStatusInAndIsDeletedFalse(Long listingId, List<ListingStatus> listingStatus);
    Optional<ProductListing> findByIdAndListingStatusAndIsDeletedFalse(Long listingId, ListingStatus listingStatus);
}
