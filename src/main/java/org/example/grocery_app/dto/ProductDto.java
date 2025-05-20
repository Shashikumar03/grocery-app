package org.example.grocery_app.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.grocery_app.entities.Inventory;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductDto implements Serializable {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private double price;

//    @URL(message = "Image URL must be a valid URL")
    private String imageUrl;

    private boolean available;
    private String unit;


    private CategoryDto categoryDto;

    private InventoryDto inventoryDto;

}
