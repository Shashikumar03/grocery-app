package org.example.grocery_app.dto;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;
import org.example.grocery_app.entities.Order;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentDto {

    private Long paymentId;


    private OrderDto orderDto;

    private String paymentMode;
    private String paymentStatus;
    private LocalDateTime paymentTime;
}
