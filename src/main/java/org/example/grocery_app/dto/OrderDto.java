package org.example.grocery_app.dto;

import lombok.*;
import org.example.grocery_app.entities.Cart;
import org.example.grocery_app.entities.Delivery;
import org.example.grocery_app.entities.Payment;
import org.example.grocery_app.entities.User;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {

    Long orderId;
//    User user;
    String OrderStatus;
    PaymentDto paymentDto;
    String paymentMode;
    DeliveryDto deliveryDto;
    LocalDateTime orderTime;
    CartDto cartDto;
    Set<CartItemDto> cartItemDto;

    private String address;
    private String landmark;
    private String mobile;
    private String city;
    private String pin;
    private String state;
    private  LocalDateTime cancelledAt;

//    private  double discountOnOrder;

    private String  note;
}
