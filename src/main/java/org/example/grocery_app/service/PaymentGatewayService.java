package org.example.grocery_app.service;



public interface PaymentGatewayService {

    String initiatePartialRefund(String paymentId, double totalAmount);
}
