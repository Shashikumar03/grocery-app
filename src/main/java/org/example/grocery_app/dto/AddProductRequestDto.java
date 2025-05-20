package org.example.grocery_app.dto;

import lombok.*;

import java.util.Set;


@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddProductRequestDto {
    private Long categoryId;
    private Set<ProductDto> productsDto;

}
