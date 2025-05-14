package org.example.grocery_app.dto;

import lombok.*;
import org.example.grocery_app.entities.Payment;

import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDetailsToAdminDto {

//    private Long orderId;

    private  UserDto userDto;


    private List<OrderDto> orderDto;





}
