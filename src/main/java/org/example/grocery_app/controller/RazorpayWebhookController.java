package org.example.grocery_app.controller;

import org.example.grocery_app.service.RazorpayWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class RazorpayWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayWebhookController.class);

    @Autowired
    private RazorpayWebhookService razorpayWebhookService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader Map<String, String> headers
    ) {
        logger.info("🔔 Webhook triggered");
        logger.info("📦 Payload: {}", payload);
        logger.info("🧾 Headers: {}", headers);

        String response = razorpayWebhookService.handleRazorpayWebHook(payload, headers);
        return ResponseEntity.ok(response);
    }
}
