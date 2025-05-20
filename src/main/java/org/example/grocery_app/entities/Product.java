package org.example.grocery_app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
//
@Entity
@Data
@ToString(exclude = "category")
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String description;
    private double price;
    private String imageUrl;

    private boolean available;
    private String unit;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;



    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Inventory inventory;
}
