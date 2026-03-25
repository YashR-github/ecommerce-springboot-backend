package com.example.ecommerce_springboot.ecommerce.repository;


import com.example.ecommerce_springboot.ecommerce.enums.PaymentStatus;
import com.example.ecommerce_springboot.ecommerce.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Optional<Payment> findTopByOrder_IdAndPaymentStatusOrderByAttemptNoDesc(Long OrderId, PaymentStatus status);

    Optional<Payment> findByGatewayReferenceId(String gatewayReferenceId);

    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);
}
