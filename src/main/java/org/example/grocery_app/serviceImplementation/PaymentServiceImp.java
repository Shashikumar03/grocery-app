package org.example.grocery_app.serviceImplementation;

import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.extern.slf4j.Slf4j;
import org.example.grocery_app.constant.PaymentMode;
import org.example.grocery_app.dto.PaymentDto;
import org.example.grocery_app.entities.Order;
import org.example.grocery_app.entities.Payment;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.OrderRepository;
import org.example.grocery_app.repository.PaymentRepository;
import org.example.grocery_app.service.PaymentService;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PaymentServiceImp implements PaymentService {

    private static final String ORDER_CONFIRMED = "CONFIRMED";

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${razorpay.secret.key}")
    private String razorpaySecretKey;

    @Override
    @Transactional
    public PaymentDto updatePayment(String razorpayId, String paymentStatus, String paymentId) {
        Payment payment = paymentRepository.findByRozerpayId(razorpayId)
                .orElseThrow(() -> new ResourceNotFoundException("razorpay", "razorpayId: " + razorpayId, 0));

        log.info("Updating payment for razorpay order id: {}", razorpayId);

        String normalizedStatus = normalizePaymentStatus(paymentStatus);
        validatePaymentStatus(normalizedStatus);

        if (PaymentMode.ONLINE.name().equals(payment.getPaymentMode())) {
            return applyOnlinePaymentUpdate(payment, normalizedStatus, paymentId);
        }

        Order order = payment.getOrder();
        if (PaymentMode.CASH_ON_DELIVERY.name().equals(payment.getPaymentMode())) {
            order.setOrderStatus(normalizedStatus);
            order.setPayment(payment);
            payment.setOrder(order);
            orderRepository.save(order);
        }

        payment.setPaymentStatus(normalizedStatus);
        payment.setPaymentId(paymentId);
        Payment saved = paymentRepository.save(payment);
        log.info("Payment updated: {}", saved.getId());
        return modelMapper.map(saved, PaymentDto.class);
    }

    @Override
    @Transactional
    public PaymentDto verifyAndCompletePayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        Payment payment = paymentRepository.findByRozerpayId(razorpayOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("razorpay", "razorpayOrderId: " + razorpayOrderId, 0));

        if (!PaymentMode.ONLINE.name().equals(payment.getPaymentMode())) {
            throw new ApiException("Payment verification is only supported for online orders.");
        }

        if ("COMPLETED".equalsIgnoreCase(payment.getPaymentStatus())
                && razorpayPaymentId.equals(payment.getPaymentId())) {
            log.info("Payment already completed for order id: {}", razorpayOrderId);
            return modelMapper.map(payment, PaymentDto.class);
        }

        verifyRazorpaySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);
        return markOnlinePaymentCompleted(payment, razorpayPaymentId);
    }

    @Override
    public PaymentDto findPaymentByOrderId(String rozarpayOrderId) {
        Payment payment = paymentRepository.findByRozerpayId(rozarpayOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("razorpay", "razorpayId" + rozarpayOrderId, 0));
        return modelMapper.map(payment, PaymentDto.class);
    }

    @Override
    public PaymentDto getPaymentByRazorpayPaymentId(String razorpayPaymentId) {
        Payment payment = paymentRepository.findByPaymentId(razorpayPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("razorpay", "razorpay :" + razorpayPaymentId, 0));
        return modelMapper.map(payment, PaymentDto.class);
    }

    @Override
    @Transactional
    public PaymentDto captureOnlinePaymentFromWebhook(String razorpayOrderId, String razorpayPaymentId, double amount) {
        Payment payment = loadOnlinePayment(razorpayOrderId);
        payment.setPaymentAmount(amount);
        return markOnlinePaymentCompleted(payment, razorpayPaymentId);
    }

    @Override
    @Transactional
    public PaymentDto failOnlinePaymentFromWebhook(String razorpayOrderId, String razorpayPaymentId) {
        Payment payment = loadOnlinePayment(razorpayOrderId);
        return markOnlinePaymentFailed(payment, razorpayPaymentId);
    }

    private PaymentDto markOnlinePaymentCompleted(Payment payment, String razorpayPaymentId) {
        if ("COMPLETED".equalsIgnoreCase(payment.getPaymentStatus())) {
            return modelMapper.map(payment, PaymentDto.class);
        }
        return applyOnlinePaymentUpdate(payment, "COMPLETED", razorpayPaymentId);
    }

    private PaymentDto markOnlinePaymentFailed(Payment payment, String razorpayPaymentId) {
        return applyOnlinePaymentUpdate(payment, "FAILED", razorpayPaymentId);
    }

    private Payment loadOnlinePayment(String razorpayOrderId) {
        Payment payment = paymentRepository.findByRozerpayId(razorpayOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "razorpayId", 0));
        if (!PaymentMode.ONLINE.name().equals(payment.getPaymentMode())) {
            throw new ApiException("Webhook payment update is only supported for online orders.");
        }
        return payment;
    }

    private PaymentDto applyOnlinePaymentUpdate(Payment payment, String normalizedStatus, String paymentId) {
        payment.setPaymentStatus(normalizedStatus);
        if (paymentId != null && !paymentId.isBlank()) {
            payment.setPaymentId(paymentId);
        }

        Order order = payment.getOrder();
        if ("COMPLETED".equals(normalizedStatus)) {
            payment.setPaymentTime(LocalDateTime.now());
            order.setOrderStatus(ORDER_CONFIRMED);
        } else if ("FAILED".equals(normalizedStatus) || "CANCELLED".equals(normalizedStatus)) {
            order.setOrderStatus("PENDING");
        }

        order.setPayment(payment);
        payment.setOrder(order);
        orderRepository.save(order);

        Payment saved = paymentRepository.save(payment);
        log.info("Online payment updated: status={}, orderId={}", normalizedStatus, order.getId());
        return modelMapper.map(saved, PaymentDto.class);
    }

    private void verifyRazorpaySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        if (razorpayOrderId == null || razorpayPaymentId == null || razorpaySignature == null
                || razorpayOrderId.isBlank() || razorpayPaymentId.isBlank() || razorpaySignature.isBlank()) {
            throw new ApiException("razorpayOrderId, razorpayPaymentId and razorpaySignature are required.");
        }

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);

            if (!Utils.verifyPaymentSignature(options, razorpaySecretKey)) {
                throw new ApiException("Invalid payment signature.");
            }
        } catch (RazorpayException e) {
            log.error("Razorpay signature verification failed", e);
            throw new ApiException("Payment verification failed: " + e.getMessage());
        }
    }

    private String normalizePaymentStatus(String paymentStatus) {
        if (paymentStatus == null || paymentStatus.isBlank()) {
            throw new ApiException("Payment status is required.");
        }
        return switch (paymentStatus.trim().toUpperCase()) {
            case "CAPTURED", "SUCCESS", "PAID" -> "COMPLETED";
            case "CREATED" -> "PENDING";
            default -> paymentStatus.trim().toUpperCase();
        };
    }

    private void validatePaymentStatus(String paymentStatus) {
        if (!"COMPLETED".equals(paymentStatus) && !"PENDING".equals(paymentStatus)
                && !"FAILED".equals(paymentStatus) && !"CANCELLED".equals(paymentStatus)) {
            throw new ApiException("Payment status should be COMPLETED, PENDING, FAILED or CANCELLED");
        }
    }
}
