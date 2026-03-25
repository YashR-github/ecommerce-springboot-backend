package com.example.ecommerce_springboot.ecommerce.controller;


import com.example.ecommerce_springboot.ecommerce.dto.*;
import com.example.ecommerce_springboot.ecommerce.services.AdminService;
import com.example.ecommerce_springboot.ecommerce.services.CustomerService;
import com.example.ecommerce_springboot.ecommerce.services.SellerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/product-listings")
public class ProductListingController {

    private final SellerService sellerService;
    private final CustomerService customerService;
    private final AdminService adminService;

    public ProductListingController(SellerService sellerService, CustomerService customerService, AdminService adminService) {
        this.sellerService = sellerService;
        this.customerService = customerService;
        this.adminService = adminService;
    }


// --------------------------------------------- CUSTOMER specific APIs ----------------------------------------------------------------------------------------------------------------------------------------------------------


    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customers/listings/search")
    public ResponseEntity<Page<CustomerSearchResultProductListingDTO>> getAllSearchBasedFilteredProductListings(@PageableDefault(size=20,page=0, sort= "createdAt", direction = Sort.Direction.DESC) Pageable pageable, String keyword) {
        Page<CustomerSearchResultProductListingDTO> productListingDTOs= customerService.getSearchRelatedListingsForCustomer(pageable,keyword);
        return ResponseEntity.ok(productListingDTOs);
    }


    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customers/listing/view-details/{productListingId}")
    public ResponseEntity<ProductListingCustomerResponseDTO> getSingleListingDetailsById(@PathVariable("productListingId") Long productListingId) {
        ProductListingCustomerResponseDTO productListingDetails = customerService.getSingleProductListingDetails(productListingId);
        return ResponseEntity.ok(productListingDetails);
    }

//---------------------------------------------- SELLER specific APIs -----------------------------------------------------------------------



    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/sellers/product-listings/filtered")
    // Pagination for getAll products  // pageSize, pageNumber, fieldName, other example- sortOrder
    public ResponseEntity<Page<SellerListingReviewSummaryDTO>> getAllSellerProductListingReviews(@ModelAttribute SellerProductListingsFilterReqDTO filterDTO){
        Page<SellerListingReviewSummaryDTO> response = sellerService.getAllAssociatedListingReviewsFiltered(filterDTO);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @PreAuthorize("hasRole('SELLER')")
    @PostMapping(value = "/sellers/product-listing/request-create")
    public ResponseEntity<SellerListingReviewSummaryDTO> requestProductListingCreationApprove(@RequestBody ProductListingCreateReqDto listingReqDto)
    {   SellerListingReviewSummaryDTO sellerProductListingResponseDTO = sellerService.requestProductListingCreation(listingReqDto.getProductId(),listingReqDto.getQuantity(), listingReqDto.getBasePrice(),listingReqDto.getTitle(), listingReqDto.getDescription(), listingReqDto.getImageUrls());
        return new ResponseEntity<>(sellerProductListingResponseDTO, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/sellers/product-listing/request-delete/{id}")
    public ResponseEntity<Void> requestProductListingDeletion(@PathVariable("id") Long listingId){
        sellerService.requestProductListingDeletion(listingId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/sellers/product-listing/update/{listingId}")
    public ResponseEntity<SellerListingReviewSummaryDTO> requestProductListingUpdation(@PathVariable("listingId") Long listingId, @RequestBody ProductListingUpdateReqDTO productListingUpdateReqDTO){
        SellerListingReviewSummaryDTO sellerReviewSummary = sellerService.requestProductListingUpdate(listingId,productListingUpdateReqDTO);
        return ResponseEntity.ok(sellerReviewSummary);
    }


//---------------------------------------------- ADMIN specific APIs -------------------------------------------------------------------------



    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/admin/product-listings/view-filtered-page")
    public ResponseEntity<Page<AdminListingReviewSummaryDTO>> getAllProductListingReviewRequests(@ModelAttribute AdminListingReviewFilterReqDTO filterDTO){
        Page<AdminListingReviewSummaryDTO> pageResult= adminService.getAllProductListingReviewRequests(filterDTO);
        return ResponseEntity.ok(pageResult);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/admin/product-listings/listing/details")
    public ResponseEntity<ProductListingAdminViewDTO> showProductListingDetails(@RequestBody AdminListingReviewAuditDecisionDTO adminResponse){
        ProductListingAdminViewDTO reviewSummaryDTO = adminService.showProductListingDetails(adminResponse.getAuditId());
        return  ResponseEntity.ok(reviewSummaryDTO);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/admin/product-listings/listing/create/approve-request")
    public ResponseEntity<AdminListingReviewSummaryDTO> handleProductListingCreateApproveRequest(@RequestBody AdminListingReviewAuditDecisionDTO adminResponse){
        AdminListingReviewSummaryDTO reviewSummaryDTO = adminService.handleCreateProductListingApproveRequest(adminResponse.getAuditId(), adminResponse.getReason());
        return  ResponseEntity.ok(reviewSummaryDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/admin/product-listings/listing/update/approve-request")
    public ResponseEntity<AdminListingReviewSummaryDTO> handleProductListingUpdateApproveRequest(@RequestBody AdminListingReviewAuditDecisionDTO adminResponse){
        AdminListingReviewSummaryDTO reviewSummaryDTO = adminService.handleUpdateProductListingApproveRequest(adminResponse.getAuditId(), adminResponse.getReason());
        return  ResponseEntity.ok(reviewSummaryDTO);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/admin/product-listings/listing/delete/approve-request")
    public ResponseEntity<AdminListingReviewSummaryDTO> handleProductListingDeleteApproveRequest(@RequestBody AdminListingReviewAuditDecisionDTO adminResponse){
        AdminListingReviewSummaryDTO reviewSummaryDTO = adminService.handleDeleteProductListingApproveRequest(adminResponse.getAuditId(), adminResponse.getReason());
        return  ResponseEntity.ok(reviewSummaryDTO);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/admin/product-listings/listing/reject-request")
    public ResponseEntity<AdminListingReviewSummaryDTO> handleProductListingRejectRequest(@RequestBody AdminListingReviewAuditDecisionDTO adminResponse){
        AdminListingReviewSummaryDTO reviewSummaryDTO = adminService.handleProductListingRejectRequest(adminResponse.getAuditId(), adminResponse.getReason());
        return  ResponseEntity.ok(reviewSummaryDTO);
    }


}
