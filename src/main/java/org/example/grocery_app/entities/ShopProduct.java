package org.example.grocery_app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ShopProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double suggestedPrice;
    private boolean available;

//    private String notes;

    @ManyToOne
    @JoinColumn(name = "shopkeeper_id")
    @JsonBackReference
    private Shopkeeper shopkeeper;

    private LocalDateTime productAssignedTime;

    private LocalDateTime productUpdatedTime;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
