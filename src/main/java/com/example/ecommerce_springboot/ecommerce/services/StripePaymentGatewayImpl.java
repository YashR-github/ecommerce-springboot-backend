package com.example.ecommerce_springboot.ecommerce.services;


import com.example.ecommerce_springboot.ecommerce.models.Order;
import com.example.ecommerce_springboot.ecommerce.repository.OrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.model.Price;
import com.stripe.param.PaymentLinkCreateParams;
import com.stripe.param.PriceCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentGatewayImpl implements PaymentService{

    private final OrderRepository orderRepository;

    @Value("${stripe.key.secret}")
    private String stripeKeySecret;

    public StripePaymentGatewayImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public String generatePaymentLink(Long orderId, Long amount) throws StripeException {
     // Below is the stripe sdk based implementation , one can also integrate stripe apis using resttemplate and using cUrl of stripe and putting it in restTemplate.exchange method
        Stripe.apiKey = stripeKeySecret;

        Order order= orderRepository.findById(orderId).orElseThrow(()->new RuntimeException("Order not found"));

        PriceCreateParams priceParams =
                PriceCreateParams.builder()
                        .setCurrency("INR")
                        .setUnitAmount(amount)
                        .setProductData(
                                PriceCreateParams.ProductData.builder().setName(orderId.toString()).build()
                        )
                        .build();
        Price price = Price.create(priceParams);


        // 3. Create payment link object

        PaymentLinkCreateParams linkParams =
                PaymentLinkCreateParams.builder()
                        .addLineItem(
                                PaymentLinkCreateParams.LineItem.builder()
                                        .setPrice(price.getId())
                                        .setQuantity(1L)
                                        .build()
                        )
                        .setAfterCompletion(  // redirects the user to specified url after payment is successful
                                PaymentLinkCreateParams.AfterCompletion.builder()
                                        .setType(PaymentLinkCreateParams.AfterCompletion.Type.REDIRECT)
                                        .setRedirect(
                                                PaymentLinkCreateParams.AfterCompletion.Redirect.builder()
                                                        .setUrl("https://google.com") // the url to direct the user when the payment is successful
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        PaymentLink paymentLink = PaymentLink.create(linkParams);

        return paymentLink.getUrl();
    }
}
