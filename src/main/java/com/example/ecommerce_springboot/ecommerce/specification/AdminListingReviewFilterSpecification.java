package com.example.ecommerce_springboot.ecommerce.specification;

import com.example.ecommerce_springboot.ecommerce.dto.AdminListingReviewFilterReqDTO;
import com.example.ecommerce_springboot.ecommerce.models.ListingReviewAudit;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class AdminListingReviewFilterSpecification implements Specification<ListingReviewAudit> {
    private final AdminListingReviewFilterReqDTO adminListingReviewFilterReqDTO;

    public AdminListingReviewFilterSpecification(AdminListingReviewFilterReqDTO adminListingReviewFilterReqDTO) {
        this.adminListingReviewFilterReqDTO = adminListingReviewFilterReqDTO;
    }

    public static Specification<ListingReviewAudit> belongsToUser(Long userId){
        return (root, query,cb) -> cb.equal(root.get("user").get("id"),userId);
    }

    @Override
    public Predicate toPredicate(Root<ListingReviewAudit> root, CriteriaQuery<?> query, CriteriaBuilder cb){

        List<Predicate> predicates = new ArrayList<>();

        if(adminListingReviewFilterReqDTO.getListingId()!=null){
            predicates.add(cb.equal(root.get("productListing").get("id"), adminListingReviewFilterReqDTO.getListingId()));
        }
        if(adminListingReviewFilterReqDTO.getReviewStatus()!=null){
            predicates.add(cb.equal(root.get("reviewStatus"), adminListingReviewFilterReqDTO.getReviewStatus()));
        }
        if(adminListingReviewFilterReqDTO.getProductId()!=null){
            predicates.add(cb.equal(root.get("product").get("id"), adminListingReviewFilterReqDTO.getProductId()));
        }
        if(adminListingReviewFilterReqDTO.getSellerId()!=null){
            predicates.add(cb.equal(root.get("productListing").get("seller").get("id"), adminListingReviewFilterReqDTO.getSellerId()));
        }
        if(adminListingReviewFilterReqDTO.getRequestedAt()!=null){
            LocalDate date = adminListingReviewFilterReqDTO.getRequestedAt();
            predicates.add(cb.between(
                    root.get("requestedAt"),
                    date.atStartOfDay(),
                    date.plusDays(1).atStartOfDay()
            ));
        }

        return cb.and(predicates.toArray(Predicate[]::new));
    }

}
