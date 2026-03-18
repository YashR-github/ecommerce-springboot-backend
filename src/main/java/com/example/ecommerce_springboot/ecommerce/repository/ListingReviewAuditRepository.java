package com.example.ecommerce_springboot.ecommerce.repository;

import com.example.ecommerce_springboot.ecommerce.enums.ActionType;
import com.example.ecommerce_springboot.ecommerce.enums.ReviewStatus;
import com.example.ecommerce_springboot.ecommerce.models.ListingReviewAudit;

import com.example.ecommerce_springboot.ecommerce.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ListingReviewAuditRepository extends JpaRepository<ListingReviewAudit, Long> {

   Boolean existsByListingIdAndActionTypeAndReviewStatus( Long productListingId, ActionType actionType, ReviewStatus reviewStatus);

   Boolean existsByProductIdAndRequestedByAndActionTypeAndReviewStatus( Long productId,  User seller, ActionType actionType, ReviewStatus reviewStatus );

//   List<ListingReviewAudit> findByReviewStatus(ReviewStatus reviewStatus);

   Page<ListingReviewAudit> findAll(Specification<ListingReviewAudit> specification, Pageable pageable);

   Optional<ListingReviewAudit> findByIdAndReviewStatus(Long auditId, ReviewStatus reviewStatus);
   Optional<ListingReviewAudit> findByIdAndActionTypeAndReviewStatus(Long auditId, ActionType actionType, ReviewStatus reviewStatus);

   Page<ListingReviewAudit> findAllByRequestedByAndIsDeletedFalse(User requestedBy,Specification<ListingReviewAudit> specification, Pageable pageable);


}
