package org.example.grocery_app.serviceImplementation;

import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.dto.PaymentDto;
import org.example.grocery_app.entities.Order;
import org.example.grocery_app.entities.Payment;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.OrderRepository;
import org.example.grocery_app.repository.PaymentRepository;
import org.example.grocery_app.service.PaymentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentServiceImp implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public PaymentDto updatePayment(String razorpayId,String paymentStatus, String paymentId) {
        Payment razorpay = this.paymentRepository.findByRozerpayId(razorpayId).orElseThrow(() -> new ResourceNotFoundException("razorpay", "razorpayId: " + razorpayId, 0));
        log.info("razorpay :{}",razorpay);
        if (!"COMPLETED".equals(paymentStatus) && !"PENDING".equals(paymentStatus) && !"FAILED".equals(paymentStatus)) {
            throw new ApiException("payment status should be ACTIVE or PENDING");
        }
        if(razorpay.getPaymentMode()=="CASH_ON_DELIVERY"){
            Order order = razorpay.getOrder();
            order.setState("COMPLETED");
            razorpay.setOrder(order);
            order.setPayment(razorpay);
            this.orderRepository.save(order);

        }

        razorpay.setPaymentStatus(paymentStatus);
        razorpay.setPaymentId(paymentId);
        Payment save = this.paymentRepository.save(razorpay);
        log.info("payment service save :{}", save);

       return this.modelMapper.map(save, PaymentDto.class);


//        return null;
    }

    @Override
    public PaymentDto findPaymentByOrderId(String rozarpayOrderId) {
        Payment razorpay = this.paymentRepository.findByRozerpayId(rozarpayOrderId).orElseThrow(() -> new ResourceNotFoundException("razorpay", "razorpayId" + rozarpayOrderId, 0));
        return this.modelMapper.map(razorpay, PaymentDto.class);

    }

    @Override
    public PaymentDto getPaymentByRazorpayPaymentId(String razorpayPaymentId) {
        Payment razorpay = this.paymentRepository.findByPaymentId(razorpayPaymentId).orElseThrow(() -> new ResourceNotFoundException("razorpay", "razorpay :" + razorpayPaymentId, 0));
         return this.modelMapper.map(razorpay, PaymentDto.class);

    }
}
