package org.example.grocery_app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "delivery_agent_id")
    private User deliveryAgent;
    
    private String deliveryStatus; // ASSIGNED, OUT_FOR_DELIVERY, DELIVERED
    private LocalDateTime deliveryTime;
}
