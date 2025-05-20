package org.example.grocery_app.dto;

import lombok.*;
import org.example.grocery_app.constant.CartStatus;
import org.example.grocery_app.entities.User;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartDto {
    private Long cartId;
    private UserDto userDto;
    private Set<CartItemDto> cartItemsDto;
    private CartStatus status;

    private double discountAmount;

    private double cartTotalPrice;
}