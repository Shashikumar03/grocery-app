package org.example.grocery_app.controller;

import org.example.grocery_app.service.RazorpayWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhook")
public class RazorpayWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayWebhookController.class);

    @Autowired
    private RazorpayWebhookService razorpayWebhookService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature,
            @RequestHeader(value = "X-Razorpay-Event-Id", required = false) String eventId
    ) {
        logger.info("Razorpay webhook received, eventId={}", eventId);

        String response = razorpayWebhookService.handleRazorpayWebHook(payload, signature, eventId);

        return switch (response) {
            case "Missing signature header" -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            case "Invalid webhook signature" -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            case "Internal server error" -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            default -> ResponseEntity.ok(response);
        };
    }
}
