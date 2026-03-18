package com.example.ecommerce_springboot.ecommerce.services;


import com.example.ecommerce_springboot.auth.util.AuthenticatedUserUtil;
import com.example.ecommerce_springboot.ecommerce.dto.*;
import com.example.ecommerce_springboot.ecommerce.enums.*;
import com.example.ecommerce_springboot.ecommerce.exceptions.CartItemNotFoundException;
import com.example.ecommerce_springboot.ecommerce.exceptions.InventoryOutOfStockException;
import com.example.ecommerce_springboot.ecommerce.exceptions.ProductListingNotFoundException;
import com.example.ecommerce_springboot.ecommerce.exceptions.ProductNotFoundException;
import com.example.ecommerce_springboot.ecommerce.models.*;
import com.example.ecommerce_springboot.ecommerce.repository.*;
import com.example.ecommerce_springboot.ecommerce.util.ObjectDtoMapperUtil;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerService {
    private final AuthenticatedUserUtil authenticatedUserUtil;
    private final InventoryItemRepository inventoryItemRepository;
    private final ObjectDtoMapperUtil objectDtoMapperUtil;
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private ProductListingRepository productListingRepository;
    private CartItemRepository cartItemRepository;
    private PaymentService paymentService;
    private OrderRepository orderRepository;

    public CustomerService(AuthenticatedUserUtil authenticatedUserUtil, InventoryItemRepository inventoryItemRepository, ObjectDtoMapperUtil objectDtoMapperUtil, ProductRepository productRepository, CartRepository cartRepository, ProductListingRepository productListingRepository, CartItemRepository cartItemRepository, @Qualifier("StripePaymentGateway") PaymentService paymentService, OrderRepository orderRepository) {
        this.authenticatedUserUtil = authenticatedUserUtil;
        this.inventoryItemRepository = inventoryItemRepository;
        this.objectDtoMapperUtil = objectDtoMapperUtil;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.productListingRepository = productListingRepository;
        this.cartItemRepository = cartItemRepository;
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
    }


//----------------------------------------------------- Catalogue/Browse service ----------------------------------------------------------------------

    @Transactional
    public List<HomePageProductDTO> getHomePageProductsDisplay(){
    List<Product> homePageProducts= productRepository.findRandomAvailableProductsWithQty(ProductStatus.PRODUCT_LIVE,10);
    return homePageProducts.stream()
            .map(objectDtoMapperUtil :: toHomePageProductDTO).
            toList();
    }


//-------------------------------------------------------- Cart service -------------------------------------------------------------------------------------------------


    @Transactional
    public Page<CartItemDTO> getCustomerCart(Pageable pageable){
        Cart cart= getActiveUserCart();
        Page<CartItem> cartItems= cartItemRepository.findByCart_IdAndIsDeletedFalseAndCartItemStatusIn(cart.getId(), List.of(CartItemStatus.RESERVED_IN_CART), pageable);
        return cartItems.map(objectDtoMapperUtil :: toCartItemDTO);
    }


    @Transactional
    public Page<CartItemDTO> addItemToCart(Long productListingId, Pageable pageable) throws ProductNotFoundException {
        ProductListing productListing = productListingRepository.findByIdAndListingStatusInAndIsDeletedFalse(productListingId, List.of(ListingStatus.ACTIVE)).orElseThrow(()-> new ProductListingNotFoundException("Product listing not found with id " + productListingId));

        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);
      int reserved = inventoryItemRepository.reserveOne(productListingId); // prevent race conditions
//        -- (Note 1)
        if(reserved==0) {
            throw new InventoryOutOfStockException("Items in current listing are out of stock");
        }

        InventoryItem inventoryItemToAdd = inventoryItemRepository.findRecentlyReserved(productListingId);

        //as quantity is available, add to the cart. now getting cart of user, or else create and get
        Cart cart= getActiveUserCart();
        CartItem existing = cart.getCartItems()
                .stream()
                .filter(ci -> ci.getProductListing().getId().equals(productListingId))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + 1);
            existing.getInventoryItems().add(inventoryItemToAdd);
            existing.setCartItemPrice(existing.getCartItemPrice().add(inventoryItemToAdd.getItemPrice()));
            inventoryItemToAdd.setReservedCartItem(existing);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setProductListing(productListing);
            newCartItem.setQuantity(1);
            newCartItem.getInventoryItems().add(inventoryItemToAdd);
            newCartItem.setCartItemStatus(CartItemStatus.RESERVED_IN_CART);
            newCartItem.setCartItemPrice(inventoryItemToAdd.getItemPrice());
            inventoryItemToAdd.setReservedCartItem(newCartItem);
            cart.getCartItems().add(newCartItem);
        }
            if (cart.getCartStatus() == CartStatus.EMPTY) {
                cart.setCartStatus(CartStatus.NON_EMPTY);
            }
            cart.setCartTotal(cart.getCartTotal().add(inventoryItemToAdd.getItemPrice()));
            inventoryItemToAdd.setItemStatus(InventoryItemStatus.RESERVED_IN_CART); //although already done by the native query
            inventoryItemRepository.save(inventoryItemToAdd);
            cartRepository.save(cart); // cascades saving cart items
            Page<CartItem> cartItems = cartItemRepository.findByCart_IdAndIsDeletedFalseAndCartItemStatusIn(cart.getId(), List.of(CartItemStatus.RESERVED_IN_CART), pageable);
            return cartItems.map(objectDtoMapperUtil::toCartItemDTO);

    }


    @Transactional  /// RECHECK
    public Page<CartItemDTO> removeFromCart(Long cartItemId, Pageable pageable){
        Optional<CartItem> optionalCartItemToDelete = cartItemRepository.findByIdAndCart_UserAndQuantityGreaterThanAndIsDeletedFalseAndCartItemStatusIn(cartItemId, getCurrentUser(),0, List.of(CartItemStatus.RESERVED_IN_CART));
        if(optionalCartItemToDelete.isEmpty()) {
            throw new CartItemNotFoundException("CartItem with id: "+ cartItemId + "not found in the user's cart");
        }
        CartItem cartItemToModify = optionalCartItemToDelete.get();
        InventoryItem itemToRemove = optionalCartItemToDelete.get().getInventoryItems().remove(0);
        Cart cart =  cartItemToModify.getCart();
        if(cartItemToModify.getInventoryItems().isEmpty()) {
            cartItemToModify.setCartItemStatus(CartItemStatus.REMOVED_FROM_CART);
            cartItemToModify.setDeleted(true);
            cart.getCartItems().remove(cartItemToModify);
        }

        itemToRemove.setItemStatus(InventoryItemStatus.AVAILABLE);
        itemToRemove.setReservedCartItem(null);
        inventoryItemRepository.save(itemToRemove);
        cart.setCartTotal(cart.getCartTotal().subtract(itemToRemove.getItemPrice()));
        if(cart.getCartItems().isEmpty()) {
            cart.setCartStatus(CartStatus.EMPTY);
        }
        Cart savedCart = cartRepository.save(cart);
        Page<CartItem> cartItems= cartItemRepository.findAllByCart_IdAndIsDeletedFalse(savedCart.getId(),pageable);
        return cartItems.map(objectDtoMapperUtil :: toCartItemDTO);
    }





//------------------------------------- Product listing service ---------------------------------------------------------------


    @Transactional
    public Page<CustomerSearchResultProductListingDTO> getSearchRelatedListingsForCustomer(Pageable pageable, String keyword){
        Page<ProductListing> pageSearchResult = productListingRepository.searchListingsByKeyword(keyword,ProductStatus.PRODUCT_LIVE,ListingStatus.ACTIVE, pageable);
        return pageSearchResult.map(objectDtoMapperUtil :: toCustomerSearchResultProductListingDTO);
    }


    @Transactional
    public ProductListingCustomerResponseDTO getSingleProductListingDetails(Long productListingId){
        ProductListing productListing  = productListingRepository.findByIdAndListingStatusInAndIsDeletedFalse(productListingId, List.of(ListingStatus.ACTIVE)).orElseThrow(()-> new ProductListingNotFoundException("Product listing is either invalid or is out of stock."));
        Integer quantityRemaining = inventoryItemRepository.findByProductListing_IdAndItemStatusInAndIsDeletedFalse(productListingId,List.of(InventoryItemStatus.AVAILABLE, InventoryItemStatus.RESERVED_IN_CART)).size();
        return ObjectDtoMapperUtil.toProductListingCustomerResponseDtO(productListing,quantityRemaining);
    }





//------------------------------------------------ Order Service -----------------------------------------------------------------
//
//    @Transactional
//    public CustomerOrderResponseDTO initiateOrderForCart() throws ExecutionException, InterruptedException {
//        Cart cart = getActiveUserCart();
//        reserveInventoryItemAndCartItemForOrder(cart);
//        Order order = createOrderSkeleton(cart);
//        List<OrderItem> orderItems= createOrderItems(cart.getCartItems(),order);
//        order.setOrderItems(orderItems);
//        order.setOrderTotal(cart.getCartTotal());
//        orderRepository.save(order);
//
//
//
//
//
//        Cart cart = cartRepository.findByUserAndIsDeletedFalseAndCartStatusNotIn(getCurrentUser(), List.of(CartStatus.EMPTY,CartStatus.INACTIVE)).orElseThrow(()-> new IllegalStateException("No active cart found for the user"));
//        List<CartItem> cartItems = cart.getCartItems();
//        CompletableFuture<List<CartItem>> updatedCartItems = markCartItemsAndAssociatedInventoryItemsStatusInOrder(cartItems);
//        CompletableFuture<List<OrderItem>> orderItems = createOrderItemsFromCartItems(updatedCartItems.get());
//
//        Order order = new Order();
//        order.setUser(getCurrentUser());
//        order.setOrderItems(orderItems.get());
//        order.setOrderTotal(cart.getCartTotal());
//
//
//
//
//
//    }
//
//
//
//
//
//    @Transactional
//    public String generatePaymentLink(Long orderId, BigDecimal amount){ //  returns payment link from stripe/razorpay
//        Cart cart = cartRepository.findByUserAndIsDeletedFalseAndCartStatusNotIn(getCurrentUser(), List.of(CartStatus.EMPTY,CartStatus.INACTIVE)).orElseThrow(()-> new IllegalStateException("No active cart found for the user"));
//        String paymentLink = paymentService.generatePaymentLink(orderId, amount);
//    }


    //webhook service
//    public String confirmPayment(){  might return payment link
//       updates payment status, inventory details, cart details, order details,etc
//    }

 //        cartItem.set(cartItem.getQuantity()+1); // add to existing quantity
//    public String makePayment(){  might return payment link
//
//    }





//     Optional<Product> optionalProduct = productRepository.findById(productListingId);
//     if(optionalProduct.isEmpty()) {
//
//         throw new ProductNotFoundException("Product not found with listing id "+productListingId);
//     }



//----------------------------------------- Helper methods ------------------------------------------------------------

//   public Order reserveInventoryItemAndCartItemForOrder(List<CartItem> cartItems){
//        List<CartItem> savedCartItems = new ArrayList<>();
//        for(CartItem cartItem: cartItems ) {
//            for(InventoryItem inventoryItem: cartItem.getInventoryItems()) {
//                inventoryItem.setItemStatus(InventoryItemStatus.RESERVED_FOR_ORDER);
//                inventoryItemRepository.save(inventoryItem);
//            }
//            cartItem.setCartItemStatus(CartItemStatus.RESERVED_IN_ORDER);
//            savedCartItems.add(cartItemRepository.save(cartItem));
//        }
//        return CompletableFuture.completedFuture(savedCartItems);
//    }

//    @Async
//    public CompletableFuture<List<CartItem>> markCartItemsAndAssociatedInventoryItemsStatusInOrder(List<CartItem> cartItems){
//        List<CartItem> savedCartItems = new ArrayList<>();
//        for(CartItem cartItem: cartItems ) {
//            for(InventoryItem inventoryItem: cartItem.getInventoryItems()) {
//                inventoryItem.setItemStatus(InventoryItemStatus.RESERVED_FOR_ORDER);
//                inventoryItemRepository.save(inventoryItem);
//            }
//            cartItem.setCartItemStatus(CartItemStatus.RESERVED_IN_ORDER);
//            savedCartItems.add(cartItemRepository.save(cartItem));
//        }
//        return CompletableFuture.completedFuture(savedCartItems);
//    }


//    @Async
//    public CompletableFuture<List<OrderItem>> createOrderItemsFromCartItems(List<CartItem> cartItems){
//        List<OrderItem> orderItems = new ArrayList<>();
//        for(CartItem cartItem : cartItems) {
//            OrderItem orderItem = new OrderItem();
//            orderItem.setCartItem(cartItem);
//            orderItem.setOrder();
//        }
//    }

    private Cart getActiveUserCart(){
        Optional<Cart> optionalCart = cartRepository.findByUserAndIsDeletedFalseAndCartStatusNotIn(getCurrentUser(), List.of(CartStatus.INACTIVE,CartStatus.SUSPENDED));
        return optionalCart.orElseGet(this::createCartForCustomer);
    }

    private Cart createCartForCustomer(){
        cartRepository.findByUser(getCurrentUser()).ifPresent(cart-> {throw new IllegalStateException("A valid active cart not found for the user and nor can be created.");});
        Cart cart = new Cart();
        cart.setUser(getCurrentUser());
        return cartRepository.save(cart);
    }


    private User getCurrentUser() {
        return authenticatedUserUtil.getCurrentUser();
    }



}



/*  (Note 1): Currently assuming only one inventory/warehouse, hence selecting the first available inventory item to add to the cart
         In case of multiple warehouses, the warehouse closest to the customer address can be used to add item to cart.Following approach will be needed in that case:
         1. Run a jpa method to find List<Warehouse> based inside cityId/stateId/countryId (based on delivery scope) and having at least 1 available item for the product listing id
         2. Get the user's exact location/pincode to get (latitude, longitude) and find the warehouse closest to the user using Dijkstra's
         3. Get an exact list of inventory items from the selected warehouse to add to the cart and update the status for those inventory items.  */
