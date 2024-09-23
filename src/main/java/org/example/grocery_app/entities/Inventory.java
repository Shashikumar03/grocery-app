package org.example.grocery_app.entities;

import jakarta.persistence.*;
import lombok.*;

@ToString(exclude = "product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
//    private Products


    private int stockQuantity;
    private int reservedStock;

}
