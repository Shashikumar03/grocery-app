package org.example.grocery_app.dto;

import lombok.*;
import org.example.grocery_app.entities.Product;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartItemDto {
    private Long cartItemId;
    private Long productId; // Only the product ID instead of the full Product entity
    private String productName; // You can add other product details if needed
    private int quantity; // Quantity of the product in the cart
    private String imageUrl;
    private BigDecimal price; // Product price, can also be stored in CartItemDto for convenience
}