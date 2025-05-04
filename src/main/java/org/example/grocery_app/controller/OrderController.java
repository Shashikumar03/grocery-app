package org.example.grocery_app.controller;

import lombok.Getter;
import org.example.grocery_app.dto.OrderDto;
import org.example.grocery_app.entities.Order;
import org.example.grocery_app.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/place-order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/{userId}/{delivery-addressId}")
    public ResponseEntity<OrderDto> getPlaceOrder(@PathVariable Long userId, @PathVariable("delivery-addressId") Long deliveryAddressId ) {
        OrderDto orderDto = this.orderService.createOrder(userId, deliveryAddressId);
        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }

    @GetMapping("history/{userId}")
    public ResponseEntity<List<OrderDto>> getOrderHistory(@PathVariable Long userId) {
        List<OrderDto> orderByUser = this.orderService.getOrderByUser(userId);
        return  new ResponseEntity<>(orderByUser, HttpStatus.OK);
    }

    @GetMapping("update/{orderId}")
    public  ResponseEntity<OrderDto> updateOrder(@PathVariable Long orderId){
        this.orderService.getOrderById(orderId);
        return null;
    }

}
