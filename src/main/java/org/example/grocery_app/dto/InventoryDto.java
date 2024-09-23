package org.example.grocery_app.dto;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InventoryDto {
    private Long id;

    private ProductDto productDto; // Product reference by ID in DTO
    private int stockQuantity;
    private int reservedStock;
}