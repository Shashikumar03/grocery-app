package org.example.grocery_app.serviceImplementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.example.grocery_app.config.EmailContentGenerator;
import org.example.grocery_app.entities.Order;
import org.example.grocery_app.entities.Payment;
import org.example.grocery_app.entities.User;
import org.example.grocery_app.entities.WebhookEvent;
import org.example.grocery_app.exception.ResourceNotFoundException;
import org.example.grocery_app.repository.PaymentRepository;
import org.example.grocery_app.repository.WebhookEventRepository;
import org.example.grocery_app.service.EmailSenderService;
import org.example.grocery_app.service.RazorpayWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class RazorpayWebhookServiceImp implements RazorpayWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayWebhookServiceImp.class);

    private final WebhookEventRepository webhookRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private EmailContentGenerator emailContentGenerator;

    @Autowired
    private EmailSenderService emailService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    public RazorpayWebhookServiceImp(WebhookEventRepository webhookRepository) {
        this.webhookRepository = webhookRepository;
    }

    @Override
    public String handleRazorpayWebHook(String payload, String signature, String eventId) {
        if (!StringUtils.hasText(signature)) {
            logger.error("Missing X-Razorpay-Signature header");
            return "Missing signature header";
        }

        if (!verifySignature(payload, signature)) {
            return "Invalid webhook signature";
        }

        String razorpayEventId = StringUtils.hasText(eventId) ? eventId : "UNKNOWN";
        if (StringUtils.hasText(eventId) && webhookRepository.existsByRazorpayEventId(eventId)) {
            logger.info("Duplicate webhook ignored: {}", eventId);
            return "Webhook already processed";
        }

        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            String eventType = rootNode.path("event").asText();

            saveWebhookEvent(payload, eventType, razorpayEventId);
            logger.info("Webhook event saved: {} ({})", eventType, razorpayEventId);

            if ("payment.captured".equals(eventType) || "payment.failed".equals(eventType)) {
                handlePaymentEvent(rootNode, eventType);
            } else if ("refund.created".equals(eventType) || "refund.processed".equals(eventType)) {
                handleRefundEvent(rootNode);
            } else {
                logger.info("Unhandled webhook event type: {}", eventType);
            }

            return "Webhook processed";
        } catch (ResourceNotFoundException e) {
            logger.warn("Webhook received but related payment not found: {}", e.getMessage());
            return "Webhook received";
        } catch (Exception e) {
            logger.error("Exception while processing webhook", e);
            return "Internal server error";
        }
    }

    private boolean verifySignature(String payload, String signature) {
        try {
            if (Utils.verifyWebhookSignature(payload, signature, webhookSecret)) {
                return true;
            }
            logger.error("Invalid webhook signature");
            return false;
        } catch (RazorpayException e) {
            logger.error("Webhook signature verification failed", e);
            return false;
        }
    }

    private void saveWebhookEvent(String payload, String eventType, String razorpayEventId) {
        WebhookEvent event = new WebhookEvent();
        event.setPayload(payload);
        event.setEventType(eventType);
        event.setRazorpayEventId(razorpayEventId);
        event.setReceivedAt(LocalDateTime.now());
        webhookRepository.save(event);
    }

    private void handlePaymentEvent(JsonNode rootNode, String eventType) {
        JsonNode paymentEntity = rootNode.path("payload").path("payment").path("entity");
        String razorpayOrderId = paymentEntity.path("order_id").asText();
        String paymentId = paymentEntity.path("id").asText();
        double amount = paymentEntity.path("amount").asDouble() / 100.0;

        Payment payment = paymentRepository.findByRozerpayId(razorpayOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "razorpayId", 0));

        payment.setPaymentId(paymentId);
        payment.setPaymentAmount(amount);
        payment.setPaymentTime(LocalDateTime.now());

        if ("payment.captured".equals(eventType)) {
            payment.setPaymentStatus("COMPLETED");
            Order order = payment.getOrder();
            User user = order.getUser();

            String htmlBody = emailContentGenerator.generateOrderConfirmationEmail(user, order);
            String[] recipients = emailContentGenerator.getOrderConfirmationRecipients();
            emailService.sendSimpleEmail(recipients, "Order Confirmation - Bazzario", htmlBody);
            logger.info("Order confirmation email sent for order ID: {}", order.getId());
        } else {
            payment.setPaymentStatus("FAILED");
        }

        paymentRepository.save(payment);
        logger.info("Payment updated: {}", payment.getId());
    }

    private void handleRefundEvent(JsonNode rootNode) {
        JsonNode refundEntity = rootNode.path("payload").path("refund").path("entity");
        String paymentId = refundEntity.path("payment_id").asText();
        double refundAmount = refundEntity.path("amount").asDouble() / 100.0;

        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "paymentId", 0));

        payment.setRefundAmount(refundAmount);
        payment.setPaymentStatus(
                refundAmount < payment.getPaymentAmount() ? "PARTIALLY_REFUNDED" : "REFUNDED"
        );
        payment.setPaymentTime(LocalDateTime.now());
        paymentRepository.save(payment);

        logger.info("Refund updated for payment ID: {}, status: {}", paymentId, payment.getPaymentStatus());
    }
}
