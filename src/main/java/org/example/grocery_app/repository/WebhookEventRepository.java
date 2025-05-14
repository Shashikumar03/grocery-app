package org.example.grocery_app.repository;

import org.example.grocery_app.entities.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {}
