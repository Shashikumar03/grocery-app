package org.example.grocery_app.controller;

import org.example.grocery_app.dto.PaymentDto;
import org.example.grocery_app.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {


    @Autowired
    private PaymentService paymentService;

    @PutMapping("/{razorpayId}/{paymentStatus}/{paymentId}")
    public ResponseEntity<PaymentDto> updatePaymentStatus(@PathVariable String razorpayId, @PathVariable String paymentStatus, @PathVariable String paymentId ){
        PaymentDto paymentDto = this.paymentService.updatePayment(razorpayId, paymentStatus, paymentId);
        return new ResponseEntity<>(paymentDto, HttpStatus.OK);
    }

    @GetMapping("/razorpay/{orderId}")
    public  ResponseEntity<PaymentDto> getRazorpayPaymentDetailsByRazorpayOrderId(@PathVariable String orderId){
        PaymentDto paymentByOrderId = this.paymentService.findPaymentByOrderId(orderId);
        return new ResponseEntity<>(paymentByOrderId, HttpStatus.OK);

    }
    @GetMapping("/{razorpay-paymentId}")
    public  ResponseEntity<PaymentDto> getPaymentDetailsByRazorpayPaymentId(@PathVariable("razorpay-paymentId") String paymentId){
        PaymentDto paymentByRazorpayPaymentId = this.paymentService.getPaymentByRazorpayPaymentId(paymentId);
        return  new ResponseEntity<>(paymentByRazorpayPaymentId, HttpStatus.OK);
    }



}
