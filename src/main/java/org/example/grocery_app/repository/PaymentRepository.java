package org.example.grocery_app.repository;

import org.example.grocery_app.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository  extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRozerpayId(String razorpayId);
    Optional<Payment> findByPaymentId(String paymentId);

}
