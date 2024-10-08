package org.example.grocery_app.dto;

import lombok.*;
import org.example.grocery_app.entities.Cart;
import org.example.grocery_app.entities.Delivery;
import org.example.grocery_app.entities.Payment;
import org.example.grocery_app.entities.User;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {

    Long orderId;
    User user;
    String OrderStatus;
    PaymentDto paymentDto;
    DeliveryDto deliveryDto;
    LocalDateTime orderTime;
    CartDto cartDto;
}
