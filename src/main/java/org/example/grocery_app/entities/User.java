package org.example.grocery_app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"carts", "order"})
@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String email;
    private String address;
    private String role;
    @Column(unique = true)
    private String phoneNumber;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Cart> carts;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    List<Order> order;
}
