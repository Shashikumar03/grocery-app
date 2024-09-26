package org.example.grocery_app.dto;

import lombok.*;
import org.example.grocery_app.constant.CartStatus;
import org.example.grocery_app.entities.User;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartDto {
    private Long id;
    private UserDto userDto;
    private Set<CartItemDto> cartItemsDto;
    private CartStatus status;
}