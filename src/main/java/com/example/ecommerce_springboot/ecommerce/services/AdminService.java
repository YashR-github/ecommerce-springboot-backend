package com.example.ecommerce_springboot.ecommerce.services;

import com.example.ecommerce_springboot.auth.util.AuthenticatedUserUtil;
import com.example.ecommerce_springboot.ecommerce.dto.*;
import com.example.ecommerce_springboot.ecommerce.enums.*;
import com.example.ecommerce_springboot.ecommerce.exceptions.*;
import com.example.ecommerce_springboot.ecommerce.models.*;
import com.example.ecommerce_springboot.ecommerce.repository.*;
import com.example.ecommerce_springboot.ecommerce.specification.AdminListingReviewFilterSpecification;
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

@Service("AdminProductService")
@PreAuthorize("hasRole('ADMIN')")
public class AdminService {

    private final AuthenticatedUserUtil authenticatedUserUtil;
    private final ProductRepository productRepository;
    private final ProductListingRepository productListingRepository;
    private final CategoryRepository categoryRepository;
    private final ListingReviewAuditRepository listingReviewAuditRepository;
    private final InventoryItemRepository inventoryItemRepository;


  public AdminService(AuthenticatedUserUtil authenticatedUserUtil, ProductRepository productRepository, ProductListingRepository productListingRepository, CategoryRepository categoryRepository, ListingReviewAuditRepository listingReviewAuditRepository, InventoryItemRepository inventoryItemRepository) {
        this.authenticatedUserUtil = authenticatedUserUtil;
        this.productRepository = productRepository;
        this.productListingRepository = productListingRepository;
        this.categoryRepository = categoryRepository;
        this.listingReviewAuditRepository = listingReviewAuditRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }


//--------------------------- Product handling =========================================================================

    @Transactional
    public ProductCreateResponseDto createNewProduct(String title, String shortDescription, String longDescription, String brand, String model,BigDecimal weightInGrams, BigDecimal avgPrice, String baseImageUrl , String categoryType){
        Optional<Product> optionalProduct = productRepository.findByTitleAndModel(title,model);

        if(optionalProduct.isPresent()) {
            throw new SellerProductAlreadyExistException("Product with same title already exist.");
        }
        User user = authenticatedUserUtil.getCurrentUser();
        Category category = categoryRepository.findByCategoryType(CategoryType.valueOf(categoryType)).orElseThrow(()->new CategoryNotFoundException("Product category not found. Please try another category or create one."));
        Product product = new Product();
        product.setTitle(title);
        product.setShortDescription(shortDescription);
        product.setLongDescription(longDescription);
        product.setBrand(brand);
        product.setModel(model);
        product.setWeightInGrams(weightInGrams);
        product.setBaseImageUrl(baseImageUrl);
        product.setProductCreator(user);
        product.setAvgPrice(avgPrice);
        product.setCategory(category);
        product.setProductStatus(ProductStatus.PRODUCT_LIVE);
        Product savedProduct = productRepository.save(product);

        return ObjectDtoMapperUtil.toDto(savedProduct);
    }


    @Transactional
    public Product updateProduct (Long productToUpdateId, String title, String shortDescription, String longDescription, String brand, BigDecimal weightInGrams, String baseImageUrl,Long categoryId, ProductStatus productStatus) {

    return null;
    }


    @Transactional
    public void markProductDeletion(Long id)  {
        Optional<Product> optionalProduct= productRepository.findByIdAndIsDeletedFalse(id);
        if(optionalProduct.isEmpty()){
            throw new ProductNotFoundException("Product not found with id "+id+". The product might have been deleted already.");
        }
        Product product= optionalProduct.get();
        product.setDeleted(true);
        productRepository.save(product);
    }


//========================================== product listing handling========================================

    @Transactional
    public Page<AdminListingReviewSummaryDTO> getAllProductListingReviewRequests(AdminListingReviewFilterReqDTO filterDTO) {
        Specification<ListingReviewAudit> specification = new AdminListingReviewFilterSpecification(filterDTO);
        Sort sort = "desc".equalsIgnoreCase(filterDTO.getSortOrder())
                ? Sort.by(filterDTO.getSortBy()).descending()
                : Sort.by(filterDTO.getSortBy()).ascending();
        Pageable pageable = PageRequest.of(filterDTO.getPageNumber(),filterDTO.getPageSize(),sort);
        Page<ListingReviewAudit> pageResult= listingReviewAuditRepository.findAll(specification,pageable);
        return pageResult.map(this::convertToAdminListingReviewSummaryDTO);
    }

    @Transactional
    public AdminListingReviewSummaryDTO handleCreateProductListingApproveRequest(Long auditId, String reason){
    User user=  authenticatedUserUtil.getCurrentUser();
    ListingReviewAudit listingRequest= verifyActionTypeAndPendingStatus(auditId, ActionType.CREATE);
    Optional<ProductListing> optionalProductListing = productListingRepository.findByIdAndListingCreatorAndListingStatusAndIsDeletedFalse(listingRequest.getListingId(), user, ListingStatus.PENDING_APPROVAL);
    if(optionalProductListing.isEmpty()) { 
        throw new ProductListingNotFoundException("Something went wrong. No associated Product Listing found");
    }
    listingRequest.setReviewStatus(ReviewStatus.APPROVED);
    listingRequest.setReason(reason);
    listingRequest.setReviewedBy(authenticatedUserUtil.getCurrentUser());
    listingRequest.setReviewedAt(java.time.LocalDateTime.now());
    listingReviewAuditRepository.save(listingRequest);
    ProductListing productListing = optionalProductListing.get();
    createInventoryItemsByQuantity(productListing);
    productListing.setListingStatus(ListingStatus.ACTIVE);
    productListingRepository.save(productListing);
    return ObjectDtoMapperUtil.getAdminListingReviewSummaryDTO(listingRequest);
    }

    @Transactional
    public AdminListingReviewSummaryDTO handleUpdateProductListingApproveRequest(Long listingReviewAuditId, String reason){
       User user=  authenticatedUserUtil.getCurrentUser();
       ListingReviewAudit listingReviewAuditRequest= verifyActionTypeAndPendingStatus(listingReviewAuditId, ActionType.UPDATE);
       listingReviewAuditRequest.setReviewStatus(ReviewStatus.APPROVED);
       listingReviewAuditRequest.setReason(reason);
       listingReviewAuditRequest.setReviewedBy(authenticatedUserUtil.getCurrentUser());
       listingReviewAuditRequest.setReviewedAt(java.time.LocalDateTime.now());
       listingReviewAuditRepository.save(listingReviewAuditRequest);
       ProductListing productListing = productListingRepository.findByIdAndListingCreatorAndListingStatusAndIsDeletedFalse(listingReviewAuditRequest.getListingId(), user, ListingStatus.PENDING_APPROVAL).orElseThrow(()-> new ProductListingNotFoundException("No associated Product Listing found."));
       productListing.setListingStatus(ListingStatus.ACTIVE);
       productListingRepository.save(productListing);
       return ObjectDtoMapperUtil.getAdminListingReviewSummaryDTO(listingReviewAuditRequest);
    }

    @Transactional
    public AdminListingReviewSummaryDTO handleDeleteProductListingApproveRequest(Long auditId, String reason){
        User user=  authenticatedUserUtil.getCurrentUser();
        ListingReviewAudit listingRequest= verifyActionTypeAndPendingStatus(auditId, ActionType.DELETE);
        listingRequest.setReviewStatus(ReviewStatus.APPROVED);
        listingRequest.setReason(reason);
        listingRequest.setReviewedBy(authenticatedUserUtil.getCurrentUser());
        listingRequest.setReviewedAt(java.time.LocalDateTime.now());
        listingReviewAuditRepository.save(listingRequest);
        deleteInventoryItems(listingRequest.getListingId());
        ProductListing productListing= productListingRepository.findByIdAndListingCreatorAndListingStatusAndIsDeletedFalse(listingRequest.getListingId(), user, ListingStatus.PENDING_APPROVAL).orElseThrow(()-> new ProductListingNotFoundException("Product Lisitng already deleted or does not exist."));
        productListing.setListingStatus(ListingStatus.DELETED);
        productListingRepository.save(productListing);
        return ObjectDtoMapperUtil.getAdminListingReviewSummaryDTO(listingRequest);
    }


    @Transactional
    public AdminListingReviewSummaryDTO handleProductListingRejectRequest(Long auditId, String reason){
        ListingReviewAudit listingRequest= verifyPendingStatus(auditId);
        listingRequest.setReviewStatus(ReviewStatus.REJECTED);
        listingRequest.setReason(reason);
        listingRequest.setReviewedBy(authenticatedUserUtil.getCurrentUser());
        listingRequest.setReviewedAt(java.time.LocalDateTime.now());
        listingReviewAuditRepository.save(listingRequest);
        return ObjectDtoMapperUtil.getAdminListingReviewSummaryDTO(listingRequest);
    }


    @Transactional
    public ProductListingAdminViewDTO showProductListingDetails(Long  reviewAuditId){
        ListingReviewAudit listingRequest= verifyPendingStatus(reviewAuditId);
        ProductListing productListing = productListingRepository.findByIdAndListingStatusAndIsDeletedFalse(listingRequest.getListingId(),ListingStatus.PENDING_APPROVAL).orElseThrow(()-> new ProductListingNotFoundException("Associated Product Listing not found"));
        ProductListingAdminViewDTO viewResponseDTO = new ProductListingAdminViewDTO();
        viewResponseDTO.setListingReviewId(listingRequest.getId());
        viewResponseDTO.setListingId(listingRequest.getListingId());
        viewResponseDTO.setSellerId(productListing.getListingCreator().getId());
        viewResponseDTO.setSellerName(productListing.getListingCreator().getName());
        viewResponseDTO.setSellerEmail(productListing.getListingCreator().getEmail());
        viewResponseDTO.setSellerJoined(productListing.getListingCreator().getCreatedAt());
        viewResponseDTO.setProductId(listingRequest.getProduct().getId());
        viewResponseDTO.setQuantity(listingRequest.getQuantity());
        viewResponseDTO.setBasePrice(productListing.getBasePrice());
        viewResponseDTO.setTitle(productListing.getTitle());
        viewResponseDTO.setDescription(productListing.getDescription());
        viewResponseDTO.setImageUrls(productListing.getImageUrls());
        viewResponseDTO.setRequestedAt(listingRequest.getRequestedAt());
        viewResponseDTO.setActionType(listingRequest.getActionType().name());
        viewResponseDTO.setListingStatus(productListing.getListingStatus().name());

        return viewResponseDTO;
    }





// ------------------------------- helper methods ===============================================================
    private void createInventoryItemsByQuantity(ProductListing productListing){
        //Note: At this point a real ecommerce system might have a warehouse system to manage real inventory items, and for each inventory item a barcode will be associated for tracking purpose.
        List<InventoryItem> inventoryItems = new ArrayList<>();
        int quantity =productListing.getQuantityListed();
        for(int i=1;i<=quantity;i++) {
            InventoryItem inventoryItem= new InventoryItem();
            inventoryItem.setItemStatus(InventoryItemStatus.AVAILABLE);
            inventoryItem.setProductListing(productListing);
            inventoryItems.add(inventoryItemRepository.save(inventoryItem));
        }
        productListing.setInventoryItems(inventoryItems);
        productListingRepository.save(productListing);
    }

    private void deleteInventoryItems(Long listingId){
      List<InventoryItem> inventoryItems = inventoryItemRepository.findByProductListing_IdAndIsDeletedFalse(listingId);
      inventoryItemRepository.deleteAll(inventoryItems); //Note: alternatively can soft-delete each inventory item if required
    }

    private ListingReviewAudit verifyPendingStatus(Long auditId){
      return listingReviewAuditRepository.findByIdAndReviewStatus(auditId, ReviewStatus.PENDING).orElseThrow(()-> new ReviewRequestNotFoundException("Review Request not found as Pending any more."));
    }

    private ListingReviewAudit verifyActionTypeAndPendingStatus(Long auditId, ActionType actionType){
      return listingReviewAuditRepository.findByIdAndActionTypeAndReviewStatus(auditId,actionType, ReviewStatus.PENDING).orElseThrow(()-> new ReviewRequestNotFoundException("Review Request not found with given action Type or a pending status any more."));
    }


     private AdminListingReviewSummaryDTO convertToAdminListingReviewSummaryDTO(ListingReviewAudit listingAudit){
          AdminListingReviewSummaryDTO adminListingReviewSummaryDTO = new AdminListingReviewSummaryDTO();

          adminListingReviewSummaryDTO.setListingId(listingAudit.getListingId());
          adminListingReviewSummaryDTO.setProductId(listingAudit.getProduct().getId());
          adminListingReviewSummaryDTO.setRequestedBy(listingAudit.getRequestedBy());
          adminListingReviewSummaryDTO.setActionType(listingAudit.getActionType().name());
          adminListingReviewSummaryDTO.setReviewedBy(listingAudit.getReviewedBy());
          adminListingReviewSummaryDTO.setReviewStatus(listingAudit.getReviewStatus().name());
          adminListingReviewSummaryDTO.setReviewedAt(listingAudit.getReviewedAt());
          adminListingReviewSummaryDTO.setReason(listingAudit.getReason());
          return adminListingReviewSummaryDTO;
      }
    }







//    private ProductListing updateAndSaveProductListing(ProductListingUpdateReqDTO reqDTO){
//        ProductListing productListing = new ProductListing();
//        if(reqDTO.getProductId()!=null)
//        { productListing.setProduct(productRepository.findByIdAndIsDeletedFalse(reqDTO.getProductId()).orElseThrow(()-> new ProductNotFoundException("Product id provided does not exist.")));}
//        if(reqDTO.getTitle()!=null && !reqDTO.getTitle().isBlank())
//        { productListing.setTitle(reqDTO.getTitle());}
//        if(reqDTO.getDescription()!=null && !reqDTO.getDescription().isBlank())
//        { productListing.setDescription(reqDTO.getDescription());}
//        if(reqDTO.getBasePrice()!=null)
//        { productListing.setBasePrice(reqDTO.getBasePrice());}
//        if(reqDTO.getImageUrls()!=null)
//        { productListing.setImageUrls(reqDTO.getImageUrls());}
//        if(reqDTO.getQuantity()!=null)
//        { productListing.setQuantityListed(reqDTO.getQuantity());}
//        return productListingRepository.save(productListing);
//    }