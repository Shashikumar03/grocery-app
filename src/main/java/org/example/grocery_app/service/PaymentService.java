package org.example.grocery_app.service;

import org.example.grocery_app.dto.PaymentDto;
import org.example.grocery_app.entities.Payment;

public interface PaymentService {


    PaymentDto updatePayment(String razoypeId, String paymentStatus, String paymentId);

    PaymentDto findPaymentByOrderId(String rozarpayOrderId);

    PaymentDto getPaymentByRazorpayPaymentId(String razorpayPaymentId);
}
