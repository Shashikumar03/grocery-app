package org.example.grocery_app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
    @JsonIgnore
    private Product product;
//    private Products

    @Min(value = 0, message = "Out of Stock")
    private int stockQuantity;

    @Min(value = 0, message = "Stock reservedStock cannot be negative")
    private int reservedStock;



}
