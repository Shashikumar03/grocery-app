package org.example.grocery_app.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
//    private List<OrderItem> items;
//
//    private String status; // PENDING, CONFIRMED, DISPATCHED, DELIVERED
//
//    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
//    private Payment payment;
//
//    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
//    private Delivery delivery;
//
//    private LocalDateTime orderTime;
}
