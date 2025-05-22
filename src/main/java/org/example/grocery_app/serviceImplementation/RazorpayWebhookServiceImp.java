package org.example.grocery_app.serviceImplementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.grocery_app.entities.Payment;
import org.example.grocery_app.entities.WebhookEvent;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.PaymentRepository;
import org.example.grocery_app.repository.WebhookEventRepository;
import org.example.grocery_app.service.RazorpayWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class RazorpayWebhookServiceImp implements RazorpayWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayWebhookServiceImp.class);

    private final WebhookEventRepository webhookRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PaymentRepository paymentRepository;

    public RazorpayWebhookServiceImp(WebhookEventRepository webhookRepository) {
        this.webhookRepository = webhookRepository;
    }

    @Override
    public String handleRazorpayWebHook(String payload, Map<String, String> headers) {
        String signature = headers.getOrDefault("x-razorpay-signature", "");

        if (signature.isEmpty()) {
            logger.error("❌ Missing 'x-razorpay-signature' header");
            return "Missing signature header";
        }

        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            String eventType = rootNode.get("event").asText();
            String eventId = headers.getOrDefault("x-razorpay-event-id", "UNKNOWN");

            // Store webhook event
            WebhookEvent event = new WebhookEvent();
            event.setPayload(payload);
            event.setEventType(eventType);
            event.setRazorpayEventId(eventId);
            event.setReceivedAt(LocalDateTime.now());
            webhookRepository.save(event);

            logger.info("📥 Webhook event saved: {}", eventType);

            // Handle Payment Events
            if ("payment.captured".equals(eventType) || "payment.failed".equals(eventType)) {
                String razorpayOrderId = rootNode.path("payload").path("payment").path("entity").path("order_id").asText();
                String paymentId = rootNode.path("payload").path("payment").path("entity").path("id").asText();
                double amount = rootNode.path("payload").path("payment").path("entity").path("amount").asDouble() / 100.0;

                Payment payment = paymentRepository.findByRozerpayId(razorpayOrderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Payment", "razorpayId", 0));

                payment.setPaymentId(paymentId);
                payment.setPaymentAmount(amount);
                payment.setPaymentTime(LocalDateTime.now());

                if ("payment.captured".equals(eventType)) {
                    payment.setPaymentStatus("COMPLETED");
                } else {
                    payment.setPaymentStatus("FAILED");
                }

                paymentRepository.save(payment);
                logger.info("💳 Payment updated: {}", payment.getId());
            }

            // Handle Refund Events
            else if ("refund.created".equals(eventType) || "refund.processed".equals(eventType)) {
                String paymentId = rootNode.path("payload").path("refund").path("entity").path("payment_id").asText();
                double refundAmount = rootNode.path("payload").path("refund").path("entity").path("amount").asDouble() / 100.0;

                Payment payment = paymentRepository.findByPaymentId(paymentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Payment", "paymentId", 0));

                // Set refund amount
                payment.setRefundAmount(refundAmount);

                // Update status based on refund amount
                if (refundAmount < payment.getPaymentAmount()) {
                    payment.setPaymentStatus("PARTIALLY_REFUNDED");

                } else {
                    payment.setPaymentStatus("REFUNDED");
                }

                payment.setPaymentTime(LocalDateTime.now());

                paymentRepository.save(payment);
                logger.info("🔁 Refund updated for Payment ID: {}, Status: {}", paymentId, payment.getPaymentStatus());
            }

            return "Webhook processed and payment/refund updated";
        } catch (Exception e) {
            logger.error("💥 Exception while processing webhook", e);
            return "Internal server error";
        }
    }
}
