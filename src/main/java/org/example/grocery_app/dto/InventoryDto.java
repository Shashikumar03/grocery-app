package org.example.grocery_app.dto;

import jakarta.validation.constraints.Min;
import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InventoryDto {
    private Long id;

    private ProductDto productDto; // Product reference by ID in DTO
    @Min(value = 0, message = "Stock quantity cannot be negative or OUT of STOCK")
    private int stockQuantity;
    private int reservedStock;
}