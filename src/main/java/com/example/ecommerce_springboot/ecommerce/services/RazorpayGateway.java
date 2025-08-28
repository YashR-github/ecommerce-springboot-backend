package com.example.ecommerce_springboot.ecommerce.services;


import com.example.ecommerce_springboot.ecommerce.models.Order;
import com.example.ecommerce_springboot.ecommerce.models.User;
import com.example.ecommerce_springboot.ecommerce.repository.OrderRepository;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service("razorpay")
public class RazorpayGateway implements PaymentService{

    private final OrderRepository orderRepository;
    private RazorpayClient razorpayClient;

    public RazorpayGateway(RazorpayClient razorpayClient, OrderRepository orderRepository) {
    this.razorpayClient = razorpayClient;
        this.orderRepository = orderRepository;
    }



    @Override
    public String generatePaymentLink(Long orderId, Long amount) throws RazorpayException {
        Order order= orderRepository.findById(orderId).orElseThrow(()->new RazorpayException("Order not found"));
        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount",amount); //10 rs
        paymentLinkRequest.put("currency","INR");
//        paymentLinkRequest.put("accept_partial",true);
//        paymentLinkRequest.put("first_min_partial_amount",100);
        paymentLinkRequest.put("expire_by",System.currentTimeMillis()+10*60*1000);//epoch in millis
        paymentLinkRequest.put("reference_id",orderId.toString());
        paymentLinkRequest.put("description","Test Payment integration of payment gateways such as Razorpay and Stripe.");

        JSONObject customer = new JSONObject();
        customer.put("name",order.getUser().getName());
//        customer.put("contact",order.getUser().getEmail());
        customer.put("email",order.getUser().getEmail());
        paymentLinkRequest.put("customer",customer);

        JSONObject notify = new JSONObject();
        notify.put("sms",true);
        notify.put("email",true);
        paymentLinkRequest.put("notify",notify);
        paymentLinkRequest.put("reminder_enable",true);

//        JSONObject notes = new JSONObject();
//        notes.put("policy_name","Jeevan Bima");
//        paymentLinkRequest.put("notes",notes);

        paymentLinkRequest.put("callback_url","https://www.google.com/");
        paymentLinkRequest.put("callback_method","get");

        PaymentLink payment = razorpayClient.paymentLink.create(paymentLinkRequest);
    return payment.toString();
    }
}
