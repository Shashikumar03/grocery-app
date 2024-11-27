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
@ToString(exclude = "delivery")
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;


//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
//    private List<OrderItem> items;
//
    private String OrderStatus; // PENDING, CONFIRMED, DISPATCHED, DELIVERED
//
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;
//
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Delivery delivery;
//
    private LocalDateTime orderTime;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "card_id")
    private  Cart cart;
}
