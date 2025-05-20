package org.example.grocery_app.controller;

import org.example.grocery_app.entities.Payment;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.PaymentRepository;
import org.example.grocery_app.service.PaymentGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/refund")
public class PaymentGatewayController {

    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @Autowired
    private PaymentRepository paymentRepository;


    @PostMapping("/partial/{paymentId}")
    public ResponseEntity<String> refundPartial(@PathVariable String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "paymentId", 0));

        String response = paymentGatewayService.initiatePartialRefund(paymentId, payment.getPaymentAmount());
        return ResponseEntity.ok(response);
    }
}
