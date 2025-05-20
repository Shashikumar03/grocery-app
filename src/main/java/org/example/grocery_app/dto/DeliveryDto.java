package org.example.grocery_app.dto;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;
import org.example.grocery_app.entities.Order;
import org.example.grocery_app.entities.User;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DeliveryDto {

    private Long deliveryId;


    private OrderDto orderDto;


    private User deliveryAgent;

    private String deliveryStatus; // ASSIGNED, OUT_FOR_DELIVERY, DELIVERED
    private LocalDateTime deliveryTime;
}
