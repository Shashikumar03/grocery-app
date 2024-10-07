package org.example.grocery_app.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "order")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    private String paymentMode; // PAYTM, UPI, CARD
    private String paymentStatus; // PENDING, COMPLETED, FAILED
    private LocalDateTime paymentTime;
}
