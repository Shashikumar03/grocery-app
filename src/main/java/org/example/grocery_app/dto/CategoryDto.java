package org.example.grocery_app.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class CategoryDto  implements Serializable {

    private Long id;

    @NotEmpty(message = "Category name cannot be empty")
    private String name;

    private String description;


    private Set<ProductDto> productsDto;

    private  String defaultUnit;

}
