package com.example.ecommerce_springboot.ecommerce.services;

import com.example.ecommerce_springboot.auth.util.AuthenticatedUserUtil;
import com.example.ecommerce_springboot.ecommerce.dto.ProductListingUpdateReqDTO;
import com.example.ecommerce_springboot.ecommerce.dto.SellerListingReviewSummaryDTO;
import com.example.ecommerce_springboot.ecommerce.dto.SellerProductListingsFilterReqDTO;
import com.example.ecommerce_springboot.ecommerce.enums.*;
import com.example.ecommerce_springboot.ecommerce.exceptions.ListingReviewAuditCreationFailureException;
import com.example.ecommerce_springboot.ecommerce.exceptions.ProductListingNotFoundException;
import com.example.ecommerce_springboot.ecommerce.exceptions.ProductNotFoundException;
import com.example.ecommerce_springboot.ecommerce.exceptions.UnAuthorizedAccessException;
import com.example.ecommerce_springboot.ecommerce.models.*;
import com.example.ecommerce_springboot.ecommerce.repository.*;
import com.example.ecommerce_springboot.ecommerce.specification.SellerListingReviewFilterSpecification;
import com.example.ecommerce_springboot.ecommerce.util.ObjectDtoMapperUtil;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("SellerProductService")
@PreAuthorize("hasRole('SELLER')")
public class SellerService {

    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductListingRepository productListingRepository;
    private final CategoryRepository categoryRepository;
    private final AuthenticatedUserUtil authenticatedUserUtil;
    private final ListingReviewAuditRepository listingReviewAuditRepository;

    public SellerService(InventoryItemRepository inventoryItemRepository, UserRepository userRepository, ProductRepository productRepository, ProductListingRepository productListingRepository, CategoryRepository categoryRepository, AuthenticatedUserUtil authenticatedUserUtil, ListingReviewAuditRepository listingReviewAuditRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productListingRepository = productListingRepository;
        this.categoryRepository = categoryRepository;
        this.authenticatedUserUtil = authenticatedUserUtil;
        this.listingReviewAuditRepository = listingReviewAuditRepository;
    }

// ------------------------------------------- Product services --------------------------------------------------------------------------








// ------------------------------------------- Product Listing services ---------------------------------------------------------------------------


    @Transactional
    public Page<SellerListingReviewSummaryDTO> getAllAssociatedListingReviewsFiltered(SellerProductListingsFilterReqDTO filterDTO) {
        Specification<ListingReviewAudit> specification = new SellerListingReviewFilterSpecification(filterDTO);
        Sort sort = "desc".equalsIgnoreCase(filterDTO.getSortOrder())
                ? Sort.by(filterDTO.getSortBy()).descending()
                : Sort.by(filterDTO.getSortBy()).ascending();
        Pageable pageable = PageRequest.of(filterDTO.getPageNumber(),filterDTO.getPageSize(),sort);
        User user = authenticatedUserUtil.getCurrentUser();
        Page<ListingReviewAudit> pageResult= listingReviewAuditRepository.findAllByRequestedByAndIsDeletedFalse(user,specification,pageable);
        return pageResult.map(ObjectDtoMapperUtil::toSellerListingReviewSummaryDTO);
    }


    @Transactional
    public SellerListingReviewSummaryDTO requestProductListingCreation(Long productId, Integer quantity, BigDecimal basePrice, String title, String description, List<String> imageUrls) {
        Optional<Product> optionalProduct = productRepository.findByIdAndIsDeletedFalse(productId);
        if (optionalProduct.isEmpty()) {
            throw new ProductNotFoundException("Product with given id not found.");
        }
        Product product = optionalProduct.get();
        User user = authenticatedUserUtil.getCurrentUser();
        User seller = userRepository.findByIdAndUserRoleAndIsDeletedFalse(user.getId(), UserRole.SELLER).orElseThrow(() -> new UnAuthorizedAccessException("User not authorized."));

        boolean exists = productListingRepository
                .existsByProductAndListingCreatorAndListingStatusIn(
                        product,
                        seller,
                        List.of(ListingStatus.PENDING_APPROVAL, ListingStatus.ACTIVE)
                );

        if (exists) {
            throw new IllegalStateException("Listing Creation request is either in review or already approved. If listing already exist, update it instead");
        }
        ProductListing productListing = new ProductListing();
        productListing.setProduct(product);
        productListing.setListingCreator(seller);
        productListing.setQuantityListed(quantity);
        productListing.setTitle(title);
        productListing.setDescription(description);
        productListing.setBasePrice(basePrice);
        productListing.setImageUrls(imageUrls);
        productListing.setListingStatus(ListingStatus.PENDING_APPROVAL);
//        productListing.setInventoryItems(new ArrayList<>());

        ProductListing savedProductListing = productListingRepository.save(productListing);

        ListingReviewAudit listingReviewAudit = generatePendingListingReviewAudit(savedProductListing, ActionType.CREATE);

        return ObjectDtoMapperUtil.toSellerListingReviewSummaryDTO(listingReviewAudit);
    }


    @Transactional
    public void requestProductListingDeletion(Long productListingId) {
        User user = authenticatedUserUtil.getCurrentUser();
        User seller = userRepository.findByIdAndUserRoleAndIsDeletedFalse(user.getId(), UserRole.SELLER).orElseThrow(() -> new UnAuthorizedAccessException("User not authorized."));
        Optional<ProductListing> optionalProductListing = productListingRepository.findByListingCreatorAndIdAndIsDeletedFalse(seller, productListingId);
        if (optionalProductListing.isEmpty()) {
            throw new ProductListingNotFoundException("No product listing found for the given id for the seller.");
        }
        //todo : in admin side soft delete products associated with the listing and soft delete listing
        //delegate to admin
        boolean exists = listingReviewAuditRepository
                .existsByListingIdAndActionTypeAndReviewStatus(
                        productListingId,
                        ActionType.DELETE,
                        ReviewStatus.PENDING
                );

        if (exists) {
            throw new IllegalStateException("Deletion request already pending.");
        }

        ListingReviewAudit listingReviewAudit = new ListingReviewAudit();
        listingReviewAudit.setListingId(productListingId);
        listingReviewAudit.setActionType(ActionType.DELETE);
        listingReviewAudit.setRequestedBy(seller);
        listingReviewAudit.setReviewedBy(null);
        listingReviewAudit.setReviewStatus(ReviewStatus.PENDING);
        listingReviewAudit.setReason(null);
        listingReviewAudit.setReviewedAt(null);

        listingReviewAuditRepository.save(listingReviewAudit);
        ProductListing productListing = optionalProductListing.get();
        productListing.setListingStatus(ListingStatus.PENDING_DELETION);
        productListingRepository.save(productListing);

    }

    @Transactional
    public SellerListingReviewSummaryDTO requestProductListingUpdate(Long listingId, ProductListingUpdateReqDTO updateDTO) {
        User user= authenticatedUserUtil.getCurrentUser();
        ProductListing originalListing = productListingRepository.findByIdAndListingCreatorAndListingStatusAndIsDeletedFalse(listingId, user, ListingStatus.ACTIVE).orElseThrow(() -> new ProductListingNotFoundException("Product Listing Id provided is incorrect or not active."));
        ProductListing updatedProductListing = new ProductListing();
        if (updateDTO.getProductId() != null) {
            updatedProductListing.setProduct(productRepository.findByIdAndIsDeletedFalse(updateDTO.getProductId()).orElseThrow(() -> new ProductListingNotFoundException("Product Id provided is incorrect.")));
        } else {
            updatedProductListing.setProduct(originalListing.getProduct());
        }
        updatedProductListing.setListingCreator(originalListing.getListingCreator());
        updatedProductListing.setTitle((updateDTO.getTitle() != null && !updateDTO.getTitle().isBlank()) ? updateDTO.getTitle() : originalListing.getTitle());
        updatedProductListing.setDescription((updateDTO.getDescription() != null && !updateDTO.getDescription().isBlank()) ? updateDTO.getDescription() : originalListing.getDescription());
        updatedProductListing.setQuantityListed(updateDTO.getQuantity() != null ? updateDTO.getQuantity() : originalListing.getQuantityListed());
        updatedProductListing.setBasePrice(updateDTO.getBasePrice() != null ? updateDTO.getBasePrice() : originalListing.getBasePrice());
        updatedProductListing.setImageUrls(updateDTO.getImageUrls() != null ? updateDTO.getImageUrls() : originalListing.getImageUrls());
        updatedProductListing.setInventoryItems(new ArrayList<>());
        updatedProductListing.setListingStatus(ListingStatus.PENDING_APPROVAL);

        originalListing.setListingStatus(ListingStatus.ARCHIVED);
        productListingRepository.save(originalListing);
        ProductListing savedProductListing = productListingRepository.save(updatedProductListing);

        ListingReviewAudit listingReviewAuditNew = generatePendingListingReviewAudit(savedProductListing, ActionType.UPDATE);

        return ObjectDtoMapperUtil.toSellerListingReviewSummaryDTO(listingReviewAuditNew);
    }


//--------------------------------------- helper methods -----------------------------------------------------------------

    public ListingReviewAudit generatePendingListingReviewAudit(ProductListing productListing, ActionType actionType) {
        ListingReviewAudit listingReviewAudit = new ListingReviewAudit();
        try {
            listingReviewAudit.setListingId(productListing.getId());
            listingReviewAudit.setActionType(actionType);
            listingReviewAudit.setRequestedBy(productListing.getListingCreator());
            listingReviewAudit.setReviewedBy(null);
            listingReviewAudit.setReviewStatus(ReviewStatus.PENDING);
            listingReviewAudit.setReason(" - ");
            listingReviewAudit.setReviewedAt(null);
        } catch (Exception e) {
            throw new ListingReviewAuditCreationFailureException("Something went wrong while creating ListingReviewAudit");
        }

        return listingReviewAuditRepository.save(listingReviewAudit);
    }


}












// To be done by warehouse people
//    public void markInventoryItemUnavailable(){
//
//    }




//    @Override
//    public Product getSingleProduct(long id) throws ProductNotFoundException {
//        //TODO check from Inventory table if product is present or not
//        Optional<Product> p= productRepository.findById(id);
//        if( p.isPresent()) {
//            return p.get();
//        }
//        throw new ProductNotFoundException("Product not found with id "+id);
//    }












 /*  1. Check if category is there in db
        2. If not there, create it and use it while saving product.
        3. If there, use it in product directly.  */

//        Optional<Category> optionalCategory= categoryRepository.findByTitle(category); // input param have category as string which is title in Category table
//        if(optionalCategory.isEmpty()){  //This means category is not present in db
//            Category newCat = new Category();
//            newCat.setTitle(category);
//            Category newRow= categoryRepository.save(newCat);
//            p.setCategory(newRow);
//        }
//        else {
//            p.setCategory(optionalCategory.get()); //if category is present in db, then set it to product object
//        }


//        Optional<ProductListing> optionalProductListing =productListingRepository.findBySellerAndProduct_IdAndQuantityAndTitle(seller, productId,quantity, title).OrElseThrow(()-> new DuplicateProductListingFoundException("It seems there is a duplicate entry with same combination of product listing data."));
//        if(optionalProductListing.isPresent()) {
//            throw new SellerProductAlreadyExistException("Product Listing with same title already exist.");
//        }
//
