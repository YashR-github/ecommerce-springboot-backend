package com.example.ecommerce_springboot.ecommerce.util;


import com.example.ecommerce_springboot.ecommerce.dto.*;
import com.example.ecommerce_springboot.ecommerce.enums.OrderStatus;
import com.example.ecommerce_springboot.ecommerce.models.*;
import com.example.ecommerce_springboot.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ObjectDtoMapperUtil {

    public static ProductListingCreateResponseDto toDto(ProductListing productListing){
        ProductListingCreateResponseDto productListingCreateResponseDto = new ProductListingCreateResponseDto();
        productListingCreateResponseDto.setListingId(productListing.getId());
        productListingCreateResponseDto.setCreatedTime(productListing.getCreatedAt());
        productListingCreateResponseDto.setProductId(productListing.getProduct().getId());
        productListingCreateResponseDto.setQuantity(productListing.getQuantityListed());
        productListingCreateResponseDto.setBasePrice(productListing.getBasePrice());
        productListingCreateResponseDto.setTitle(productListing.getTitle());
        productListingCreateResponseDto.setDescription(productListing.getDescription());
        productListingCreateResponseDto.setImageUrls(productListing.getImageUrls());
        return productListingCreateResponseDto;
    }

    public static ProductCreateResponseDto toDto(Product product){
        ProductCreateResponseDto productCreateResponseDto = new ProductCreateResponseDto();
        productCreateResponseDto.setProductId(product.getId());
        productCreateResponseDto.setCreatedAt(product.getCreatedAt());
        productCreateResponseDto.setTitle(product.getTitle());
        productCreateResponseDto.setShortDescription(product.getShortDescription());
        productCreateResponseDto.setLongDescription(product.getLongDescription());
        productCreateResponseDto.setBrand(product.getBrand());
        productCreateResponseDto.setModel(product.getModel());
        productCreateResponseDto.setWeightInGrams(product.getWeightInGrams());
        productCreateResponseDto.setBaseImageUrl(product.getBaseImageUrl());
        productCreateResponseDto.setCategoryId(product.getCategory().getId());
        return productCreateResponseDto;

    }
    public static ProductListingCustomerResponseDTO toProductListingCustomerResponseDtO(ProductListing productListing, Integer quantityRemaining){
        ProductListingCustomerResponseDTO responseDTO = new ProductListingCustomerResponseDTO();
        responseDTO.setListingId(productListing.getId());
        responseDTO.setSellerName(productListing.getListingCreator().getName());
        responseDTO.setSellerID(productListing.getListingCreator().getId());
        responseDTO.setSellerEmail(productListing.getListingCreator().getEmail());
        responseDTO.setCategory(productListing.getProduct().getCategory().toString());
        responseDTO.setBrand(productListing.getProduct().getBrand());
        responseDTO.setModel(productListing.getProduct().getModel());
        responseDTO.setWeightInGrams(productListing.getProduct().getWeightInGrams());
        responseDTO.setQuantityRemaining(quantityRemaining);
        responseDTO.setTitle(productListing.getTitle());
        responseDTO.setDescription(productListing.getDescription());
        responseDTO.setPrice(productListing.getBasePrice());
        responseDTO.setImageUrls(productListing.getImageUrls());
        responseDTO.setListingStatus(productListing.getListingStatus().name());
        return responseDTO;
    }


    public static  AdminListingReviewSummaryDTO getAdminListingReviewSummaryDTO(ListingReviewAudit listingRequest) {
        AdminListingReviewSummaryDTO summaryDTO = new AdminListingReviewSummaryDTO();
        summaryDTO.setListingId(listingRequest.getListingId());
        summaryDTO.setProductId(listingRequest.getProduct().getId());
        summaryDTO.setActionType(listingRequest.getActionType().name());
        summaryDTO.setRequestedBy(listingRequest.getRequestedBy());
        summaryDTO.setReviewStatus(listingRequest.getReviewStatus().name());
        summaryDTO.setReason(listingRequest.getReason());
        summaryDTO.setReviewedAt(listingRequest.getReviewedAt());
        return summaryDTO;
    }

    public static SellerListingReviewSummaryDTO toSellerListingReviewSummaryDTO(ListingReviewAudit listingReviewAudit){
        SellerListingReviewSummaryDTO sellerListingReviewSummaryDTO = new SellerListingReviewSummaryDTO();
        sellerListingReviewSummaryDTO.setReviewId(listingReviewAudit.getId());
        sellerListingReviewSummaryDTO.setListingId(listingReviewAudit.getListingId());
        sellerListingReviewSummaryDTO.setProductId(listingReviewAudit.getProduct().getId());
        sellerListingReviewSummaryDTO.setActionType(listingReviewAudit.getActionType().name());
        sellerListingReviewSummaryDTO.setQuantity(listingReviewAudit.getQuantity());
        sellerListingReviewSummaryDTO.setRequestedAt(listingReviewAudit.getRequestedAt());
        sellerListingReviewSummaryDTO.setReviewedAt(listingReviewAudit.getReviewedAt());
        sellerListingReviewSummaryDTO.setReviewStatus(listingReviewAudit.getReviewStatus().name());
        sellerListingReviewSummaryDTO.setReason(listingReviewAudit.getReason());
        return sellerListingReviewSummaryDTO;
    }


    public CartItemDTO toCartItemDTO(CartItem cartItem){
        CartItemDTO cartItemDto = new CartItemDTO();
        cartItemDto.setListingId(cartItem.getProductListing().getId());
        cartItemDto.setListingTitle(cartItem.getProductListing().getTitle());
        cartItemDto.setQuantity(cartItem.getQuantity());
        cartItemDto.setImageUrl(cartItem.getProductListing().getImageUrls().get(0));
        cartItemDto.setUpdatedAt(cartItem.getUpdatedAt());
        cartItemDto.setCartItemPrice(cartItem.getCartItemPrice());
        return cartItemDto;
    }

    public HomePageProductDTO toHomePageProductDTO(Product homePageProduct){
        HomePageProductDTO homePageProductDTO = new HomePageProductDTO();
        homePageProductDTO.setProductId(homePageProduct.getId());
        homePageProductDTO.setTitle(homePageProduct.getTitle());
        homePageProductDTO.setCategory(homePageProduct.getCategory().toString());
        homePageProductDTO.setBrand(homePageProduct.getBrand());
        homePageProductDTO.setModel(homePageProduct.getModel());
        homePageProductDTO.setImageUrl(homePageProduct.getBaseImageUrl());
        homePageProductDTO.setPrice(homePageProduct.getAvgPrice());
        homePageProductDTO.setShortDescription(homePageProduct.getShortDescription());
        return homePageProductDTO;
    }

    public CustomerSearchResultProductListingDTO toCustomerSearchResultProductListingDTO(ProductListing productListing){
        CustomerSearchResultProductListingDTO  responseDTO = new CustomerSearchResultProductListingDTO ();
        responseDTO.setListingId(productListing.getId());
        responseDTO.setTitle(productListing.getTitle());
        responseDTO.setDescription(productListing.getDescription());
        responseDTO.setImageUrl(productListing.getImageUrls().get(0));
        responseDTO.setPrice(productListing.getBasePrice());
        responseDTO.setCategory(productListing.getProduct().getCategory().toString());
        responseDTO.setBrand(productListing.getProduct().getBrand());
        responseDTO.setModel(productListing.getProduct().getModel());
        return responseDTO;
    }

    public static CustomerOrderResponseDTO toCustomerOrderResponseDTO(CustomerOrder order){
        CustomerOrderResponseDTO responseDTO = new CustomerOrderResponseDTO();
        responseDTO.setOrderId(order.getId());
        responseDTO.setOrderItems(order.getOrderItems().stream().map(ObjectDtoMapperUtil :: toOrderItemDTO).toList());
        responseDTO.setOrderStatus(OrderStatus.CREATED.name());
        responseDTO.setTotalOrderAmount(order.getOrderTotal());
        return responseDTO;
    }

    public static OrderItemDTO toOrderItemDTO(OrderItem orderItem){
        OrderItemDTO dto = new OrderItemDTO();
        dto.setListingId(orderItem.getProductListing().getId());
        dto.setTitle(orderItem.getProductListing().getTitle());
        dto.setQuantity(orderItem.getQuantity());
        dto.setUpdatedAt(orderItem.getUpdatedAt());
        dto.setTotalPrice(orderItem.getPriceAtPurchase());
        return dto;
    }
}
