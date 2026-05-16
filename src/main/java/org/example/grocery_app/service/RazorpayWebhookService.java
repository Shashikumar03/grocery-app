package org.example.grocery_app.service;

public interface RazorpayWebhookService {

    String handleRazorpayWebHook(String payload, String signature, String eventId);
}
