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
import com.razorpay.RazorpayException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerService {

    private final AuthenticatedUserUtil authenticatedUserUtil;
    private final InventoryItemRepository inventoryItemRepository;
    private final ObjectDtoMapperUtil objectDtoMapperUtil;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final ProductListingRepository productListingRepository;
    private final CartItemRepository cartItemRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final CustomerOrderRepository customerOrderRepository;
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public CustomerService(AuthenticatedUserUtil authenticatedUserUtil, InventoryItemRepository inventoryItemRepository, ObjectDtoMapperUtil objectDtoMapperUtil, ProductRepository productRepository, CartRepository cartRepository, ProductListingRepository productListingRepository, CartItemRepository cartItemRepository, @Qualifier("stripe") PaymentService paymentService, PaymentRepository paymentRepository, CustomerOrderRepository customerOrderRepository) {
        this.authenticatedUserUtil = authenticatedUserUtil;
        this.inventoryItemRepository = inventoryItemRepository;
        this.objectDtoMapperUtil = objectDtoMapperUtil;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.productListingRepository = productListingRepository;
        this.cartItemRepository = cartItemRepository;
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.customerOrderRepository = customerOrderRepository;
    }

    //----------------------------------------------------- Product- Catalogue/Homepage service ----------------------------------------------------------------------

    @Transactional
    public List<HomePageProductDTO> getHomePageProductsDisplay() {
        List<Product> homePageProducts = productRepository.findRandomAvailableProductsWithQty(ProductStatus.PRODUCT_LIVE, 10);
        return homePageProducts.stream()
                .map(objectDtoMapperUtil::toHomePageProductDTO).
                toList();
    }

//-------------------------------------------------------- Cart service -------------------------------------------------------------------------------------------------


    @Transactional
    public Page<CartItemDTO> getCustomerCart(Pageable pageable) {
        Cart cart = getActiveUserCart();
        Page<CartItem> cartItems = cartItemRepository.findByCart_IdAndIsDeletedFalseAndCartItemStatusIn(cart.getId(), List.of(CartItemStatus.RESERVED_IN_CART), pageable);
        return cartItems.map(objectDtoMapperUtil::toCartItemDTO);
    }


    @Transactional
    public Page<CartItemDTO> addItemToCart(Long productListingId, Pageable pageable) throws ProductNotFoundException {
        ProductListing productListing = productListingRepository.findByIdAndListingStatusInAndIsDeletedFalse(productListingId, List.of(ListingStatus.ACTIVE)).orElseThrow(() -> new ProductListingNotFoundException("Product listing not found with id " + productListingId));
        Cart cart = getActiveUserCart();

        CartItem existing = cart.getCartItems()
                .stream()
                .filter(ci -> !ci.isDeleted() && ci.getCartItemStatus() == CartItemStatus.RESERVED_IN_CART && ci.getProductListing().getId().equals(productListingId))
                .findFirst()
                .orElse(null);
        CartItem cartItem;
        if (existing != null) {
            cartItem = existing;
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProductListing(productListing);
            cartItem.setQuantity(0);
            cartItem.setCartItemStatus(CartItemStatus.RESERVED_IN_CART);
            cartItem.setCartItemPrice(BigDecimal.ZERO);
            cart.getCartItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusMinutes(15);
        int reserved = inventoryItemRepository.reserveOneForCart(productListingId, cartItem.getId(), expiry);
        if (reserved == 0) {
            throw new InventoryOutOfStockException("Items in current listing are out of stock");
        }
        InventoryItem inventoryItemToAdd = inventoryItemRepository.findReservedItemForCartItem(cartItem.getId());
        cartItem.setQuantity(cartItem.getQuantity() + 1);
        cartItem.getInventoryItems().add(inventoryItemToAdd);
        cartItem.setCartItemPrice(cartItem.getCartItemPrice().add(inventoryItemToAdd.getItemPrice()));

        inventoryItemToAdd.setReservedCartItem(cartItem);
        inventoryItemToAdd.setItemStatus(InventoryItemStatus.RESERVED_IN_CART);
        inventoryItemToAdd.setReservedAt(now);
        inventoryItemToAdd.setReservationExpiryTime(expiry);

        if (cart.getCartStatus() == CartStatus.EMPTY) {
            cart.setCartStatus(CartStatus.NON_EMPTY);
        }
        cart.setCartTotal(cart.getCartTotal().add(inventoryItemToAdd.getItemPrice()));

        inventoryItemRepository.save(inventoryItemToAdd);
        cartRepository.save(cart); // cascades saving cart items
        Page<CartItem> cartItems = cartItemRepository.findByCart_IdAndIsDeletedFalseAndCartItemStatusIn(cart.getId(), List.of(CartItemStatus.RESERVED_IN_CART), pageable);
        return cartItems.map(objectDtoMapperUtil::toCartItemDTO);

    }


    @Transactional
    public Page<CartItemDTO> removeFromCart(Long cartItemId, Pageable pageable) {
        CartItem cartItemToModify = cartItemRepository.findByIdAndCart_UserAndQuantityGreaterThanAndIsDeletedFalseAndCartItemStatusIn(cartItemId, getCurrentUser(), 0, List.of(CartItemStatus.RESERVED_IN_CART)).orElseThrow(() -> new CartItemNotFoundException("CartItem with id: " + cartItemId + "not found in the user's cart"));
        Cart cart = cartItemToModify.getCart();
        InventoryItem itemToRemove = inventoryItemRepository.findTopByReservedCartItem_IdAndItemStatusOrderByIdDesc(
                        cartItemId,
                        InventoryItemStatus.RESERVED_IN_CART)
                .orElseThrow(() -> new IllegalStateException("Reserved inventory not found for cart item " + cartItemId));

        int released = inventoryItemRepository.releaseCartReservationForInventory(itemToRemove.getId());
        if (released == 0) {
            throw new IllegalStateException("Inventory release failed");
        }
        cartItemToModify.getInventoryItems().remove(itemToRemove);
        cartItemToModify.setQuantity(cartItemToModify.getQuantity() - 1);
        cartItemToModify.setCartItemPrice(cartItemToModify.getCartItemPrice().subtract(itemToRemove.getItemPrice()));

        if (cartItemToModify.getQuantity() == 0) {
            cartItemToModify.setCartItemStatus(CartItemStatus.REMOVED_FROM_CART);
            cartItemToModify.setDeleted(true);
            cart.getCartItems().remove(cartItemToModify);
        }
        cart.setCartTotal(cart.getCartTotal().subtract(itemToRemove.getItemPrice()));
        if (cart.getCartItems().isEmpty()) {
            cart.setCartStatus(CartStatus.EMPTY);
        }
        cartRepository.save(cart);
        Page<CartItem> cartItems = cartItemRepository.findByCart_IdAndIsDeletedFalseAndCartItemStatusIn(
                cart.getId(),
                List.of(CartItemStatus.RESERVED_IN_CART),
                pageable
        );
        return cartItems.map(objectDtoMapperUtil::toCartItemDTO);
    }


//------------------------------------------------ Product listing service --------------------------------------------------------------------------------------------


    @Transactional
    public Page<CustomerSearchResultProductListingDTO> getSearchRelatedListingsForCustomer(Pageable pageable, String keyword) {
        Page<ProductListing> pageSearchResult = productListingRepository.searchListingsByKeyword(keyword, ProductStatus.PRODUCT_LIVE, ListingStatus.ACTIVE, pageable);
        return pageSearchResult.map(objectDtoMapperUtil::toCustomerSearchResultProductListingDTO);
    }


    @Transactional
    public ProductListingCustomerResponseDTO getSingleProductListingDetails(Long productListingId) {
        ProductListing productListing = productListingRepository.findByIdAndListingStatusInAndIsDeletedFalse(productListingId, List.of(ListingStatus.ACTIVE)).orElseThrow(() -> new ProductListingNotFoundException("Product listing is either invalid or is out of stock."));
        Integer quantityRemaining = inventoryItemRepository.findByProductListing_IdAndItemStatusInAndIsDeletedFalse(productListingId, List.of(InventoryItemStatus.AVAILABLE, InventoryItemStatus.RESERVED_IN_CART)).size();
        return ObjectDtoMapperUtil.toProductListingCustomerResponseDtO(productListing, quantityRemaining);
    }


//------------------------------------------------ Order Service -------------------------------------------------------------------------------------

    @Transactional
    public CustomerOrderResponseDTO createOrderForCart() throws ExecutionException, InterruptedException {
        Cart cart = cartRepository.findActiveCartForCheckout(getCurrentUser(),CartStatus.NON_EMPTY).orElseThrow(()-> new IllegalStateException("No active cart found for the user"));

        if (cart.getCartItems().isEmpty()) {
            if (cart.getCartStatus() == CartStatus.NON_EMPTY) {
                cart.setCartStatus(CartStatus.EMPTY);
            }
            throw new IllegalStateException("Cart is empty. Please add items to cart before proceeding.");
        }
        LocalDateTime now = LocalDateTime.now();
        int expiredCount = inventoryItemRepository.countExpiredReservationsForCart(cart.getId(), now);// todo should remove expired items too
        if (expiredCount > 0) {
            throw new IllegalStateException("Some items in the cart have expired. Please refresh cart.");
        }
        CustomerOrder order = createOrderAndOrderItemsFromCart(cart);
        LocalDateTime orderExpiryTime = now.plusMinutes(10);
        for (CartItem cartItem : cart.getCartItems()) {
            if (cartItem.isDeleted() || cartItem.getCartItemStatus() != CartItemStatus.RESERVED_IN_CART) {
                continue;
            }
            inventoryItemRepository.freezeInventoryForOrder(order.getId(), cartItem.getId(), orderExpiryTime);
        }
        cart.setCartStatus(CartStatus.CHECKOUT_INITIATED);
        cartRepository.save(cart);
        return ObjectDtoMapperUtil.toCustomerOrderResponseDTO(order);

    }

    @Transactional
    public String initiatePaymentForOrder(Long orderId) throws StripeException, RazorpayException {
        LocalDateTime now = LocalDateTime.now();
        CustomerOrder order = customerOrderRepository.findByIdForPayment(orderId).orElseThrow(()-> new IllegalStateException("Order not found."));
        if(order.getOrderStatus() != OrderStatus.CREATED){
            throw new IllegalStateException("Order not eligible for payment");
        }
        int expired = inventoryItemRepository.countExpiredOrderReservations(orderId,now);
        if(expired>0){
            cancelOrderInternals(order,"Reservation expired");
            throw new IllegalStateException("Order reservation expired. Please re-checkout.");
        }
        //find active payment attempt
        Optional<Payment> activePayment = paymentRepository.findTopByOrder_IdAndPaymentStatusOrderByAttemptNoDesc(orderId,PaymentStatus.PENDING);
        if(activePayment.isPresent()){
            Payment payment = activePayment.get();
            return payment.getPaymentLink();
        }
        int nextAttempt = order.getPayments().size()+1;
        String paymentLink = paymentService.generatePaymentLink(order.getId(),order.getOrderTotal());
        Payment payment = generatePaymentRow(order,nextAttempt,paymentLink);
        order.getPayments().add(payment);
        customerOrderRepository.save(order);
        return paymentLink;
    }



    //webhook
    @Transactional
    public void handleStripeWebhook(String rawPayload, String signatureHeader){
        Event event;
        try{
            event = Webhook.constructEvent(rawPayload,signatureHeader, webhookSecret);
        }
        catch(SignatureVerificationException e){
            throw new IllegalStateException("Webhook signature verification failed");
        }
        String type = event.getType();
        switch(type){
            case "checkout.session.completed":
                handleCheckoutSessionCompleted(event,PaymentStatus.COMPLETED);
                break;

            case "checkout.session.expired":
                handleCheckoutSessionExpired(event,PaymentStatus.EXPIRED);
                break;

            case "payment_intent.payment_failed":
                handlePaymentFailure(event,PaymentStatus.FAILED);
                break;

            case "payment_intent.canceled":
                handlePaymentFailure(event,PaymentStatus.CANCELLED);
                break;
        }

    }




//----------------------------------------- Helper methods ------------------------------------------------------------


    public void handleCheckoutSessionCompleted(Event event, PaymentStatus paymentStatus){
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow(()-> new IllegalStateException("Invalid session object"));
        String gatewayReferenceId= session.getId();
        Payment payment = paymentRepository.findByGatewayReferenceId(gatewayReferenceId)
                .orElseThrow(()-> new IllegalStateException("Payment not found"));
        if(payment.getPaymentStatus() == PaymentStatus.COMPLETED){
            return;
        }
        CustomerOrder order = payment.getOrder();
        Cart cart = order.getUser().getCart();

        payment.setPaymentStatus(paymentStatus);
        payment.setGateway(PaymentGateway.STRIPE);
        payment.setCompletedAt(LocalDateTime.now());
        inventoryItemRepository.markInventorySold(order.getId());
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setPaymentStatus(PaymentStatus.COMPLETED);

        if(cart!=null){
            cart.setCartStatus(CartStatus.EMPTY);
            cart.setCartTotal(BigDecimal.ZERO);
            cart.getCartItems().forEach(ci-> {ci.setDeleted(true);ci.setCartItemStatus(CartItemStatus.ORDER_PLACED);});
        cart.getCartItems().clear();
        }
        customerOrderRepository.save(order);
        paymentRepository.save(payment);
    }

    public void handleCheckoutSessionExpired(Event event, PaymentStatus paymentStatus){
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow(()-> new IllegalStateException("Invalid session object"));
        String gatewayReferenceId= session.getId();
        Payment payment = paymentRepository.findByGatewayReferenceId(gatewayReferenceId).orElseThrow(()-> new IllegalStateException("Payment not found"));
        if(payment.getPaymentStatus() == PaymentStatus.COMPLETED){
            return ;
        }
        CustomerOrder order =payment.getOrder();
        payment.setPaymentStatus(paymentStatus);
        payment.setFailureReason("Checkout session expired");
        cancelOrderAndReleaseInventory(order);
        paymentRepository.save(payment);
    }

    public void handlePaymentFailure(Event event, PaymentStatus paymentStatus){
       PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElseThrow(()-> new IllegalStateException("Invalid payment intent"));
       Payment payment= paymentRepository.findByGatewayTransactionId(intent.getId()).orElseThrow(()-> new IllegalStateException("Payment not found"));
       if(payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
           return;
       }
       payment.setPaymentStatus(paymentStatus);
       payment.setFailureReason(intent.getLastPaymentError() !=null ? intent.getLastPaymentError().getMessage() : "Payment failed");
       paymentRepository.save(payment);
   }


    public void cancelOrderAndReleaseInventory(CustomerOrder order){
        inventoryItemRepository.releaseOrderReservations(order.getId());
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.EXPIRED);
        order.setCancellationReason("Checkout session expired");
        Cart cart = order.getUser().getCart();
        if(cart !=null){
            cart.setCartStatus(CartStatus.NON_EMPTY);
        }
        customerOrderRepository.save(order);
    }

    public CustomerOrder createOrderAndOrderItemsFromCart(Cart cart){
        CustomerOrder order = new CustomerOrder();
        order.setUser(getCurrentUser());
        order.setOrderStatus(OrderStatus.CREATED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderTotal(cart.getCartTotal());
        order.setCreatedAt(LocalDateTime.now());
        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItem cartItem : cart.getCartItems()) {
            if(cartItem.isDeleted() || cartItem.getCartItemStatus() != CartItemStatus.RESERVED_IN_CART){
                continue;
            }
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProductListing(cartItem.getProductListing());
            oi.setQuantity(cartItem.getQuantity());
            oi.setPriceAtPurchase(cartItem.getCartItemPrice());
            orderItems.add(oi);
        }
        order.setOrderItems(orderItems);
        return customerOrderRepository.save(order);
    }

    private void cancelOrderInternals(CustomerOrder order, String reason) {

        inventoryItemRepository.releaseOrderReservations(order.getId());

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.EXPIRED);
        order.setCancellationReason(reason);

        Cart cart = order.getUser().getCart();
        cart.setCartStatus(CartStatus.NON_EMPTY);
        customerOrderRepository.save(order);
    }

    private Payment generatePaymentRow(CustomerOrder order, int nextAttempt,String paymentLink){
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(order.getOrderTotal());
        payment.setAttemptNo(nextAttempt);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setPaymentLink(paymentLink);
        payment.setGateway(PaymentGateway.STRIPE);
        return paymentRepository.save(payment);
    }


    private Cart getActiveUserCart(){
        Optional<Cart> optionalCart = cartRepository.findByUserAndIsDeletedFalseAndCartStatusNotIn(getCurrentUser(), List.of(CartStatus.INACTIVE,CartStatus.SUSPENDED));
        return optionalCart.orElseGet(this::createCartForCustomer);
    }

    private Cart createCartForCustomer(){
        cartRepository.findByUser(getCurrentUser()).ifPresent(cart-> {throw new IllegalStateException("A valid active cart not found for the user and nor can be created.");});
        Cart cart = new Cart();
        cart.setCartStatus(CartStatus.EMPTY);
        cart.setCartTotal(BigDecimal.ZERO);
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

