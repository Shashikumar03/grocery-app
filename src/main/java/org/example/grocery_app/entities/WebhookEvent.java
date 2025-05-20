package org.example.grocery_app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//import javax.persistence.*;
import java.time.LocalDateTime;


@Setter
@Getter
@ToString
@Entity
public class WebhookEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob // Store large payloads
    private String payload;

    private String eventType;
    private String razorpayEventId;
    private LocalDateTime receivedAt;

    // Getters and setters
}
