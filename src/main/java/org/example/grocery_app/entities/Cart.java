package org.example.grocery_app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.grocery_app.constant.CartStatus;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<CartItem> CartItems;

    @Enumerated(EnumType.STRING)
    private CartStatus status;

    private double discountAmount;

    public double getTotalPricesOfAllProduct() {
        // Summing the total price of all cart items
        double sum = CartItems.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
        return Math.max(0, sum - discountAmount);

    }
    


}
