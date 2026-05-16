package org.example.grocery_app.service;

import org.example.grocery_app.repository.WebhookEventRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface RazorpayWebhookService {

    String  handleRazorpayWebHook(String payload, Map<String, String> headers);
}
