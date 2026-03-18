package com.example.ecommerce_springboot.ecommerce.specification;

import com.example.ecommerce_springboot.ecommerce.dto.SellerProductListingsFilterReqDTO;
import com.example.ecommerce_springboot.ecommerce.models.ListingReviewAudit;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SellerListingReviewFilterSpecification implements Specification<ListingReviewAudit> {

    private final SellerProductListingsFilterReqDTO sellerProductListingsFilterReqDTO;

    public SellerListingReviewFilterSpecification(SellerProductListingsFilterReqDTO sellerProductListingsFilterReqDTO) {
        this.sellerProductListingsFilterReqDTO = sellerProductListingsFilterReqDTO;
    }

    public static Specification<ListingReviewAudit> belongsToUser(Long userId){
        return (root, query,cb) -> cb.equal(root.get("user").get("id"),userId);
    }


    @Override
    public Predicate toPredicate(Root<ListingReviewAudit> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<>();
        if(sellerProductListingsFilterReqDTO.getReviewId()!=null){
            predicates.add(cb.equal(root.get("id"),sellerProductListingsFilterReqDTO.getReviewId()));
        }
        if(sellerProductListingsFilterReqDTO.getListingId()!=null){
            predicates.add(cb.equal(root.get("productListing").get("id"),sellerProductListingsFilterReqDTO.getListingId()));
        }
        if(sellerProductListingsFilterReqDTO.getProductId()!=null){
            predicates.add(cb.equal(root.get("product").get("id"),sellerProductListingsFilterReqDTO.getProductId()));
        }
        if(sellerProductListingsFilterReqDTO.getKeyword()!=null){
            predicates.add(cb.like(root.get("productListing").get("title"),"%"+sellerProductListingsFilterReqDTO.getKeyword()+"%"));
        }
        if(sellerProductListingsFilterReqDTO.getReviewStatus()!=null){
            predicates.add(cb.equal(root.get("reviewStatus"),sellerProductListingsFilterReqDTO.getReviewStatus()));
        }
        if(sellerProductListingsFilterReqDTO.getActionType()!=null){
            predicates.add(cb.equal(root.get("actionType"),sellerProductListingsFilterReqDTO.getActionType()));
        }

        if(sellerProductListingsFilterReqDTO.getCategory()!=null){
            predicates.add(cb.equal(root.get("category"),sellerProductListingsFilterReqDTO.getCategory()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));

    }

}
